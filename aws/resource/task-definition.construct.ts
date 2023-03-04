import { Duration, RemovalPolicy } from "aws-cdk-lib";
import { IRepository, Repository } from "aws-cdk-lib/aws-ecr";
import {
  AppProtocol,
  Compatibility,
  ContainerImage,
  CpuArchitecture,
  LogDriver,
  OperatingSystemFamily,
  Protocol,
  Secret,
  TaskDefinition,
  TaskDefinitionProps,
} from "aws-cdk-lib/aws-ecs";
import { PolicyDocument, PolicyStatement, Role, ServicePrincipal } from "aws-cdk-lib/aws-iam";
import { LogGroup, RetentionDays } from "aws-cdk-lib/aws-logs";
import { DatabaseSecret } from "aws-cdk-lib/aws-rds";
import { Bucket } from "aws-cdk-lib/aws-s3";
import { Secret as SecretManager } from "aws-cdk-lib/aws-secretsmanager";
import { Construct } from "constructs";
import env, { isProd } from "../env";

type TaskDefinitionConstructProps = {
  repositoryArn: string;
  bucket: Bucket;
  secret: SecretManager;
};

const environment = env.environment;
const port = env.port;
const imageTag = env.imageTag;
const { id, name } = env.stack;
const family = `${name}-${environment}`;
const assumedBy = new ServicePrincipal("ecs-tasks.amazonaws.com");
const logPrefix = "/ecs/fargate";

export class TaskDefinitionConstruct extends TaskDefinition {
  constructor(scope: Construct, props: TaskDefinitionConstructProps) {
    const { repositoryArn, bucket, secret } = props;
    const memoryMiB = isProd() ? "2 GB" : "0.5 GB";
    const cpu = isProd() ? "1 vCPU" : "0.25 vCPU";
    const repository = getRepository(scope, repositoryArn);
    const image = getImage(repository);
    const logGroup = createLogGroup(scope);
    const taskRole = createTaskRole(scope);
    const executionRole = createExecutionRole(scope, logGroup, repository);
    const config: TaskDefinitionProps = {
      family,
      compatibility: Compatibility.FARGATE,
      memoryMiB,
      cpu,
      runtimePlatform: {
        cpuArchitecture: CpuArchitecture.ARM64,
        operatingSystemFamily: OperatingSystemFamily.LINUX,
      },
      taskRole,
      executionRole,
    };

    super(scope, `${id}TaskDefinition-${environment}`, config);

    this.container(scope, image, logGroup, secret);
    this.trustPolicy(taskRole, executionRole);
    this.grantBucketAccess(taskRole, bucket);
  }

  private container(scope: Construct, image: ContainerImage, logGroup: LogGroup, secret: SecretManager) {
    const credentials = getCredentials(secret);
    const { username, password } = getAuroraCredentials(scope);

    this.addContainer(`${id}Container-${environment}`, {
      containerName: name,
      image,
      logging: LogDriver.awsLogs({
        streamPrefix: logPrefix,
        logGroup,
      }),
      portMappings: [
        {
          name: "http",
          containerPort: port,
          hostPort: port,
          protocol: Protocol.TCP,
          appProtocol: AppProtocol.http,
        },
      ],
      healthCheck: {
        command: ["CMD-SHELL", "wget -qO- http://localhost/actuator/health || exit 1"],
        startPeriod: Duration.seconds(isProd() ? 20 : 300),
      },
      environment: {
        SPRING_PROFILES_ACTIVE: environment,
        AWS_EC2_METADATA_DISABLED: "true",
      },
      secrets: {
        AUTH_ACCESS_TOKEN_PRIVATE_KEY: credentials.accessToken.privateKey,
        AUTH_ACCESS_TOKEN_PUBLIC_KEY: credentials.accessToken.publicKey,
        AUTH_REFRESH_TOKEN_PRIVATE_KEY: credentials.refreshToken.privateKey,
        AUTH_REFRESH_TOKEN_PUBLIC_KEY: credentials.refreshToken.publicKey,
        ENCRYPT_KEY: credentials.encrypt.key,
        ENCRYPT_SALT: credentials.encrypt.salt,
        SPRING_DATASOURCE_PRIMARY_USERNAME: username,
        SPRING_DATASOURCE_PRIMARY_PASSWORD: password,
        SPRING_DATASOURCE_READONLY_USERNAME: username,
        SPRING_DATASOURCE_READONLY_PASSWORD: password,
      },
    });
  }

  private trustPolicy(taskRole: Role, executionRole: Role) {
    const trustPolicy = new PolicyStatement({
      actions: ["sts:AssumeRole"],
      resources: [this.taskDefinitionArn],
    });

    taskRole.addToPolicy(trustPolicy);
    executionRole.addToPolicy(trustPolicy);
  }

  private grantBucketAccess(role: Role, bucket: Bucket) {
    bucket.grantReadWrite(role);
    bucket.grantPut(role);
    bucket.grantDelete(role);
  }
}

const createLogGroup = (scope: Construct) => {
  return new LogGroup(scope, `${id}LogGroup-${environment}`, {
    logGroupName: `${logPrefix}/${family}`,
    removalPolicy: RemovalPolicy.DESTROY,
    retention: RetentionDays.ONE_MONTH,
  });
};

const createTaskRole = (scope: Construct) => {
  return new Role(scope, `${id}TaskRole-${environment}`, {
    roleName: `${id}TaskRole-${environment}`,
    assumedBy,
  });
};

const createExecutionRole = (scope: Construct, logGroup: LogGroup, repository: IRepository) => {
  return new Role(scope, `${id}ExecutionRole-${environment}`, {
    roleName: `${id}ExecutionRole-${environment}`,
    assumedBy,
    inlinePolicies: {
      [`${id}ExecutionRolePolicy-${environment}`]: new PolicyDocument({
        statements: [
          new PolicyStatement({
            actions: [
              "ecr:BatchCheckLayerAvailability",
              "ecr:BatchGetImage",
              "ecr:GetAuthorizationToken",
              "ecr:GetDownloadUrlForLayer",
            ],
            resources: [repository.repositoryArn],
          }),
          new PolicyStatement({
            actions: ["logs:CreateLogStream", "logs:PutLogEvents"],
            resources: [logGroup.logGroupArn],
          }),
        ],
      }),
    },
  });
};

const getRepository = (scope: Construct, repositoryArn: string) => {
  return Repository.fromRepositoryArn(scope, `${id}Repository-${environment}`, repositoryArn);
};

const getImage = (repository: IRepository) => {
  return ContainerImage.fromEcrRepository(repository, imageTag);
};

const getCredentials = (secret: SecretManager) => {
  const accessTokenPrivateKey = Secret.fromSecretsManager(secret, "accessToken.privateKey");
  const accessTokenPublicKey = Secret.fromSecretsManager(secret, "accessToken.publicKey");
  const refreshTokenPrivateKey = Secret.fromSecretsManager(secret, "refreshToken.privateKey");
  const refreshTokenPublicKey = Secret.fromSecretsManager(secret, "refreshToken.publicKey");
  const encryptKey = Secret.fromSecretsManager(secret, "encrypt.key");
  const encryptSalt = Secret.fromSecretsManager(secret, "encrypt.salt");

  return {
    accessToken: {
      privateKey: accessTokenPrivateKey,
      publicKey: accessTokenPublicKey,
    },
    refreshToken: {
      privateKey: refreshTokenPrivateKey,
      publicKey: refreshTokenPublicKey,
    },
    encrypt: {
      key: encryptKey,
      salt: encryptSalt,
    },
  };
};

const getAuroraCredentials = (scope: Construct) => {
  const credential = DatabaseSecret.fromSecretNameV2(
    scope,
    `${id}AuroraSecret-${environment}`,
    `api-aurora-${environment}`
  );
  const username = Secret.fromSecretsManager(credential, "username");
  const password = Secret.fromSecretsManager(credential, "password");

  return {
    username,
    password,
  };
};
