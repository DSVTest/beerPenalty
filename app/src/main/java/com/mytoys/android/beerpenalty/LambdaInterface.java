package com.mytoys.android.beerpenalty;

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction;
public interface LambdaInterface {

    /**
     * Invoke the Lambda function "AndroidBackendLambdaFunction".
     * The function name is the method name.
     */
    @LambdaFunction(functionName = "addBeer-bp")
    String addBeer(Name name);
}
