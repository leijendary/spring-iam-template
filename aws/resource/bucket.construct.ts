import { RemovalPolicy } from "aws-cdk-lib";
import { BlockPublicAccess, Bucket, BucketEncryption } from "aws-cdk-lib/aws-s3";
import { Construct } from "constructs";
import env, { isProd } from "../env";

const { id, name } = env.stack;
const environment = env.environment;
const organization = env.organization;

export class BucketConstruct extends Bucket {
  constructor(scope: Construct) {
    super(scope, `${id}Bucket-${environment}`, {
      bucketName: `${organization}-${name}-${environment}`,
      blockPublicAccess: BlockPublicAccess.BLOCK_ALL,
      encryption: BucketEncryption.KMS,
      removalPolicy: RemovalPolicy.RETAIN,
      versioned: isProd(),
    });
  }
}
