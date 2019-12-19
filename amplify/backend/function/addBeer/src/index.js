/* Amplify Params - DO NOT EDIT
You can access the following resource attributes as environment variables from your Lambda function
var environment = process.env.ENV
var region = process.env.REGION
var authBeerpenaltyc78cb28bUserPoolId = process.env.AUTH_BEERPENALTYC78CB28B_USERPOOLID

Amplify Params - DO NOT EDIT */

const environment = process.env.ENV
const region = "eu-central-1"
const URL = require('url');
const aws = require('aws-sdk');
const gql = require('graphql-tag');
const AWSAppSyncClient = require('aws-appsync').default;
require('isomorphic-fetch');

const cognitoidentityserviceprovider = new aws.CognitoIdentityServiceProvider({ apiVersion: '2016-04-18' })
const cognitoSP = new aws.CognitoIdentityServiceProvider(
  {region: "eu-central-1"}
)

// structured params for the adminInitiateAuth method
const initiateAuthParams = {
  AuthFlow:   "ADMIN_NO_SRP_AUTH",
  ClientId:   process.env.clientID,    
  UserPoolId: process.env.userPoolID, 
  AuthParameters: {
    USERNAME: process.env.username,  
    PASSWORD: process.env.password, 
  }
};

// Get an Id Token (JWT) for the user
function getCredentials() {
  return new Promise((resolve, reject) => {
    cognitoSP.adminInitiateAuth(initiateAuthParams, (authErr, authData) => {
      if (authErr) {
        console.log(authErr)
        reject(authErr)
      } else if (authData === null) {
        reject("Auth data is null")
      } else {
        console.log("Auth Successful")
        resolve(authData)
      }
    })
  })
}

function setID(name) {
	var id = ""
	switch(name) {
		case "Debora":
		id = "d66644d0-410a-40e2-8c61-502ad46dd2a3";
		break;
		case "Jeldrik":
		id = "7956005b-2e57-441d-95d2-efad9fbe0efc";
		break;
		case "Victor":
		id = "c339e254-16fa-434c-9709-e6f6af5485b3";
		break;
		case "Cesar":
		id = "13f04a50-4bae-4f38-8fd1-c0424976767b";
		break;
		case "David":
		id = "5bd04b2d-044d-4432-a46e-e335762be714";
		break;
		case "Noda":
		id = "f477ab0f-9cd6-412c-bd3b-a0278523c877"
		break;
		case "Marina":
		id = "6154ea17-cfd5-4e7e-a275-673f7930aed4";
		break;
		case "Flo":
		id = "4abc6b92-f325-4b16-8a7c-79adb8fe624e";
		break;
	}

	return id
}

exports.handler = async function (event, context) { //eslint-disable-line

const url = "https://rcbefik57ncrdoayj7hmwlm7va.appsync-api.eu-central-1.amazonaws.com/graphql"

let cred
let credExpirationDate = new Date('01-01-1970') // to keep track of if credentials are out of date

const appsyncClient = new AWSAppSyncClient(
  {
    url,
    region,
    auth: {
      type: "AMAZON_COGNITO_USER_POOLS",
      jwtToken: async() => {
        // check if we already have credentials or if credentials are expired
        if (!cred || credExpirationDate < new Date()) {
          // get new credentials
          cred = await getCredentials()
          // give ourselves a 10 minute leeway here
          credExpirationDate = new Date(+new Date() + (cred.AuthenticationResult.ExpiresIn - 600) * 1000)
        }
        return cred.AuthenticationResult.IdToken
      },
    },
    disableOffline: true,
  },
  {
    defaultOptions: {
      query: {
        fetchPolicy: 'network-only',
        errorPolicy: 'all',
      },
    },
  }
);

var identifier = setID(event.name)

  const mutation = gql(`mutation addBeer($input: AddBeerInput!) {
  						addBeer(input: $input) {
  							id
  							}
						}`);

await appsyncClient.mutate({
  mutation,
  variables: {
  	input: {
    	id: identifier
    	}
  }
});

};
