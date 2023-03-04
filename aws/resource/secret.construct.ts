import { SecretValue } from "aws-cdk-lib";
import { Secret } from "aws-cdk-lib/aws-secretsmanager";
import { Construct } from "constructs";
import env from "../env";

const { id, name } = env.stack;
const environment = env.environment;

export class SecretConstruct extends Secret {
  constructor(scope: Construct) {
    super(scope, `${id}Secret-${environment}`, {
      secretName: `${name}-${environment}`,
      description: "Identity and access management",
      secretObjectValue: {
        "accessToken.privateKey": SecretValue.unsafePlainText(""),
        "accessToken.publicKey": SecretValue.unsafePlainText(""),
        "refreshToken.privateKey": SecretValue.unsafePlainText(""),
        "refreshToken.publicKey": SecretValue.unsafePlainText(""),
        "encrypt.key": SecretValue.unsafePlainText(""),
        "encrypt.salt": SecretValue.unsafePlainText(""),
      },
    });
  }
}
