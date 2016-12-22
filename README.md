# Table of Contents
1. [Payment SDK for Android](#PaymentSDK)
  * [Download the latest SDK](#DownloadSDK)
  * [Creating a Project and Adding Library Using Android Studio](#CreateProject)
  * [Using The SDK in Sandbox Mode](#SandBoxMode)
  
2. [Using the SDK with UI (In PCI-DSS Scope: No )](#SDKWithUI)   
   * [Pay with Card/Wallet](#Pay)
   * [Pay with Card](#PayWithCard)
   * [Pay with Wallet](#PayWithWallet)
   * [Validate Card](#ValidateCard)
   * [Pay With Token](#PayWithToken)
   
3. [Using the SDK without UI (In PCI-DSS Scope: Yes)](#SDKWithOutUI)
   * [Pay with Card/Token](#PayWithCardToken)
   * [Pay with Wallet](#PayWithWalletNoUI)
   * [Validate Card and Get Token](#ValidateCardNoUI)
   * [Authorize PayWithCard using OTP](#AuthorizeOTP)  
   * [Authorize Card Validation using OTP] (#ValidateCardOTP)
   * [Authorize PayWithWallet using OTP](#AuthorizeWOTP)
   * [Checking Payment Status](#PaymentStatus)
3. [Using Android SDK to Create Blackberry Application](#BlackBerry)
   
## <a name='PaymentSDK'></a> Payment SDK for Android

Interswitch payment SDK allows you to accept payments from customers within your mobile application.
**Please Note: *The current supported currency is naira (NGN), support for other currencies would be added later***
The first step to ​using the ​Android SDK is to register as a merchant. This is described [here] (merchantxuat.interswitchng.com)


### <a name='DownloadSDK'></a> Download the latest SDK


Download the latest SDK from the link below

https://github.com/techquest/isw-payment-sdk-android/releases

It consists of ​a library:

1. *deviceprint-release-2.2.0.aar*


**Use Android Studio’s dependency management tool (Gradle) to add the library to your project.**

 
### <a name='CreateProject'></a> Creating a Project and Adding Library Using Android Studio

1. Download **Android Studio 1.2.2** or later
2. Create a New Project
3. Put **deviceprint-release-2.2.0.aar** in the libs folder of the app.
4. To add the **deviceprint-release-2.2.0.aar** library to your project, navigate to **File -> New -> New Module -> Import .JAR/.AAR Package** option in Android Studio.
5. Select the **deviceprint-release-2.2.0.aar** in libs folder
6. To add the jar files, edit the build.gradle file of your app and add
```java
    repositories {
        maven {
            url  'http://dl.bintray.com/techquest/maven-repo'
        }
    }
    dependencies{
        compile 'com.interswitchng:payment-android:0.0.9'
        compile 'com.android.support:appcompat-v7:23.1.1'
        compile 'com.android.support:design:23.1.1'
    }    
```
7. Finally, rebuild the project


### <a name='SandBoxMode'></a> Using The SDK in Sandbox Mode

During development of your app, you should use the SDK in sandbox mode to enable testing. Different Client Id and Client Secret are provided for Production and Sandbox mode. The procedure to use the SDK on sandbox mode is just as easy:

* Use Sandbox Client Id and Client Secret got from the Sandbox Tab of the Developer Console after signup(usually you have to wait for 5 minutes after signup for you to see the Sandbox details) everywhere you are required to supply Client Id and Client Secret in the remainder of this documentation              
* In your code, override the api base as follows
```java
    Passport.overrideApiBase(Passport.SANDBOX_API_BASE); 
    Payment.overrideApiBase(Payment.SANDBOX_API_BASE); 
```
* Follow the remaining steps in the documentation.
* NOTE: When going into Production mode, use the Client Id and the Client Secret got from the Production Tab of Developer Console instead.



## <a name='SDKWithUI'></a>Using the SDK with UI (In PCI-DSS Scope: No )

### <a name='Pay'>Pay with Card/Wallet

* To allow for Payment with Card or Wallet
* Create a Pay button
* In the onClick listener of the Pay button, use this code.

  Note: Supply your Client Id and Client Secret you got after registering as a Merchant

```java
    RequestOptions options = RequestOptions.builder()
    .setClientId("IKIA335B188FDC3527EDB1E9300D35F6C51826DFC8A5")
    .setClientSecret("4HOFYiMJitFQeHYUCH/pvTF6jpiIaZqzVKB/pheK4Cs=")
    .build();
    Pay pay = new Pay(activity, customerId, paymentDescription, amount, currency, options, 
    new IswCallback<PurchaseResponse>()  {
        @Override
        public void onError(Exception error) {
            // Handle error.
            // Payment not successful.
        }

        @Override
        public void onSuccess(PurchaseResponse response) {
        /* Handle success.
           Payment successful. The response object contains fields transactionIdentifier, 
           message, amount, token, tokenExpiryDate, panLast4Digits, transactionRef and cardType. 
           Save the token, tokenExpiryDate, cardType and panLast4Digits 
           in order to pay with the token in the future.
        */
       }
    });
    pay.start();
```


### <a name='PayWithCard'>Pay with Card
    
* To allow for Payment with Card only
* Create a Pay button
* In the onClick listener of the Pay button, use this code.

  Note: Supply your Client Id and Client Secret you got after registering as a Merchant

```java
    RequestOptions options = RequestOptions.builder()
    .setClientId("IKIA14BAEA0842CE16CA7F9FED619D3ED62A54239276")
    .setClientSecret("Z3HnVfCEadBLZ8SYuFvIQG52E472V3BQLh4XDKmgM2A=")
    .build();
    PayWithCard payWithCard = new PayWithCard(activity, customerId, paymentDescription, amount, 
    currency, options, new IswCallback<PurchaseResponse>() {
        @Override
        public void onError(Exception error) {
            // Handle error.
            // Payment not successful.
        }
        
        @Override
        public void onSuccess(final PurchaseResponse response) {
            /* Handle success
               Payment successful. The response object contains fields transactionIdentifier, 
               message, amount, token, tokenExpiryDate, panLast4Digits, transactionRef and cardType. 
               Save the token, tokenExpiryDate, cardType and panLast4Digits in order to 
               pay with the token in the future.
            */
        }
    });
    payWithCard.start();
```


### <a name='PayWithWallet'>Pay With Wallet

* To allow for Payment with Wallet only
* Create a Pay button
* In the onClick listener of the Pay button, use this code.

  Note: Supply your Client Id and Client Secret you got after registering as a Merchant

```java
    RequestOptions options = RequestOptions.builder()
    .setClientId("IKIA14BAEA0842CE16CA7F9FED619D3ED62A54239276")
    .setClientSecret("Z3HnVfCEadBLZ8SYuFvIQG52E472V3BQLh4XDKmgM2A=")
    .build();
    PayWithWallet payWithWallet = new PayWithWallet(activity, customerId, paymentDescription, amount, 
    currency, options, new IswCallback<PurchaseResponse>() {
        @Override
        public void onError(Exception error) {
            // Handle error
            // Payment not successful.
        }

        @Override
        public void onSuccess(PurchaseResponse response) {
            /* Handle success
               Payment successful. The response object contains fields transactionIdentifier, 
               message, amount, token, tokenExpiryDate, panLast4Digits, otpTransactionIdentifier, 
               transactionRef and cardType. 
               Save the token, tokenExpiryDate, cardType and panLast4Digits 
               in order to pay with the token in the future.
            */
        }
    });
    payWithWallet.start();
```

### <a name='ValidateCard'></a>Validate Card 

* Validate card is used to check if a card is a valid card, it returns the card balance and token
* To call validate card, use this code.

  Note: * Supply your Client Id and Client Secret you got after registering as a Merchant
**Please Note: *Balance is not returned for VISA Card***

```java
    RequestOptions options = RequestOptions.builder()
    .setClientId("IKIAD6DC1B942D95035FBCC5A4449C893D36536B5D54")
    .setClientSecret("X1u1M6UNyASzslufiyxZnLb3u78TYODVnbRi7OxLNew=")
    .build();
    ValidateCard validateCard = new ValidateCard(activity, customerId, options, 
    new IswCallback<ValidateCardResponse>() {
        @Override
        public void onError(Exception error) {
            // Handle error.
            // Card validation not successful
        }
        @Override
        public void onSuccess(final ValidateCardResponse response) {
            /* Handle success.
               Card validation successful. The response object contains fields token, tokenExpiryDate
                panLast4Digits, transactionRef, balance and cardType. Save the token, tokenExpiryDate, cardType 
                and panLast4Digits in order to pay with the token in the future.
            */
        }
    });
    validateCard.start();
```


### <a name='PayWithToken'></a> Pay with Token

* To allow for Payment with Token only
* Create a Pay button
* In the onClick listener of the Pay button, use this code.

  Note: Supply your Client Id and Client Secret you got after registering as a Merchant

```java
    RequestOptions options = RequestOptions.builder()
    .setClientId("IKIAD6DC1B942D95035FBCC5A4449C893D36536B5D54")
    .setClientSecret("X1u1M6UNyASzslufiyxZnLb3u78TYODVnbRi7OxLNew=")
    .build();
    PayWithToken payWithToken = new PayWithToken(activity, customerId, amount, token, expiryDate, currency, 
    cardType panLast4Digits, paymentDescription, options, new IswCallback<PurchaseResponse>() {
        @Override
        public void onError(Exception error) {
            // Handle error
            // Payment not successful.
        }

        @Override
        public void onSuccess(final PurchaseResponse response) {
            /* Handle success
               Payment successful. The response object contains fields transactionIdentifier,
               message, amount, token, tokenExpiryDate, panLast4Digits, transactionRef and cardType. 
               Save the token, tokenExpiryDate cardType and panLast4Digits 
               in order to pay with the token in the future.
            */
        }
    });
    payWithToken.start();
```



## <a name='SDKWithOutUI'></a>Using the SDK without UI (In PCI-DSS Scope: Yes)


### <a name='PayWithCardToken'></a>Pay with Card/Token

* To allow for Payment with Card or Token
* Create a UI to collect amount and card details
* Create a Pay button
* In the onClick listener of the Pay button, use this code.

Note: Supply your Client Id and Client Secret you got after registering as a Merchant

```java
    RequestOptions options = RequestOptions.builder()
    .setClientId("IKIA3E267D5C80A52167A581BBA04980CA64E7B2E70E")
    .setClientSecret("SagfgnYsmvAdmFuR24sKzMg7HWPmeh67phDNIiZxpIY=")
    .build();
    PurchaseRequest request = new PurchaseRequest(); // Setup request parameters
    request.setCustomerId("1234567890"); // Optional email, mobile no, BVN etc to uniquely identify the customer.
    request.setAmount("100"); // Amount in Naira
    request.setCurrency("NGN"); // ISO Currency code
    request.setPan("5060100000000000012"); //Card No or Token
    request.setPinData("1111"); // Optional Card PIN for card payment
    request.setExpiryDate("2004"); // Card or Token expiry date in YYMM format
    request.setRequestorId("11179920172"); // Requestor Identifier 
    request.setCvv2("111");
    request.setTransactionRef(RandomString.numeric(12)); // Generate a unique transaction reference.
    Context context = this; // Reference to your Android Activity

    new PaymentSDK(context, options).purchase(request, new IswCallback<PurchaseResponse>() { 
            //Send payment
            @Override
            public void onError(Exception error) {
                // Handle error and notify the user.
                // Payment not successful.
            }

            @Override
            public void onSuccess(PurchaseResponse response) {
                // Check if OTP is required.
                if (StringUtils.hasText(response.getResponseCode())) {                
                   if (PaymentSDK.SAFE_TOKEN_RESPONSE_CODE.equals(response.getResponseCode())) {
                        // OTP required, ask user for OTP and authorize transaction
                        // See how to authorize transaction with OTP below.
                   }
                   else if (PaymentSDK.CARDINAL_RESPONSE_CODE.equals(response.getResponseCode())) {
                        // redirect user to cardinal authorization page
                        // See how to authorize transaction with Cardinal below.
                   }                   
                }
                else {
                     // OTP not required.
                     // Handle and notify user of successful transaction. 
                     // A token for the card details is returned in the response.
                }
                // The response object contains fields transactionIdentifier, message, 
                // amount, token, tokenExpiryDate, panLast4Digits, otpTransactionIdentifier, 
                // transactionRef and cardType. Save the token, tokenExpiryDate, cardType and 
                // panLast4Digits in order to pay with the token in the future.
            }
    });
```

### <a name='PayWithWalletNoUI'></a>Pay with Wallet

* To allow for Payment with Wallet only
* Create a UI to collect amount, CVV, expiry date and PIN and to display user's Payment Method(s). Use the code below to load the Payment Method(s) array in a Spinner

Note: Supply your Client Id and Client Secret you got after registering as a Merchant

```java
    RequestOptions options = RequestOptions.builder()
    .setClientId("IKIA3E267D5C80A52167A581BBA04980CA64E7B2E70E")
    .setClientSecret("SagfgnYsmvAdmFuR24sKzMg7HWPmeh67phDNIiZxpIY=")
    .build();
    //Load Wallet
    final WalletRequest request = new WalletRequest();
    request.setTransactionRef(RandomString.numeric(12)); // Generate a unique transaction reference
    Context context = this; // Reference to your Android Activity
    new WalletSDK(context, options).getPaymentMethods(request, new IswCallback<WalletResponse>() {
            @Override
            public void onError(Exception error) {
                // Handle error
                // Unable to get payment methods
            }

            @Override
            public void onSuccess(WalletResponse response) {
                PaymentMethod[] paymentMethods = response.getPaymentMethods();
                //Display payment methods in a Spinner
            }
    });
```


* Create a Pay button
* In the onClick listener of the Pay button, use this code.

```java    
    final PurchaseRequest request = new PurchaseRequest(); 
    //Setup request parameters using the selected Payment Method   
    //Optional email, mobile no, BVN etc to uniquely identify the customer.
    request.setCustomerId("1234567890");   
    request.setAmount("100"); //Amount in Naira
    request.setCurrency("NGN"); // ISO Currency code
    if (paymethodSpinner.getSelectedItem() == null) {
        // Notify user no Payment Method selected.
        return;
    }
    request.setPan(((PaymentMethod) paymethodSpinner.getSelectedItem()).getToken()); //Card Token
    request.setPinData(pin.getText().toString()); //Card PIN
    request.setTransactionRef(RandomString.numeric(12)); // Generate a unique transaction reference.
    //Send payment
    new WalletSDK(context, options).purchase(request, new IswCallback<PurchaseResponse>() {
            @Override
            public void onError(Exception error) {
                // Handle and notify user of error
            }

            @Override
            public void onSuccess(PurchaseResponse response) {
                if (StringUtils.hasText(response.getOtpTransactionIdentifier())) {
                    //OTP required
                    //Ask user for OTP and authorize transaction using the otp Transaction Identifier
                } else { 
                    //OTP not required
                   //Handle and notify user of successful transaction
                }
            }
    );
```

### <a name='ValidateCardNoUI'></a> Validate Card and Get Token
* To check if a card is valid and get a token
* Create a UI to collect card details
* Create a Validate/Add Card button
* In the onClick listener of the Validate/Add Card button, use this code.


Note: Supply your Client Id and Client Secret you got after registering as a Merchant
**Please Note: *Balance is not returned for VISA Card***
```java
    RequestOptions options = RequestOptions.builder()
    .setClientId("IKIA3E267D5C80A52167A581BBA04980CA64E7B2E70E")
    .setClientSecret("SagfgnYsmvAdmFuR24sKzMg7HWPmeh67phDNIiZxpIY=")
    .build();
    ValidateCardRequest request = new ValidateCardRequest(); // Setup request parameters
    request.setCustomerId("1234567890"); // Optional email, mobile no, BVN etc to uniquely identify the customer.
    request.setPan("5060100000000000012"); //Card No or Token
    request.setPinData("1111"); // Optional Card PIN for card payment
    request.setExpiryDate("2004"); // Card or Token expiry date in YYMM format
    request.setCvv2("111"); // Card Verification Value
    request.setTransactionRef(RandomString.numeric(12)); // Generate a unique transaction reference.
    Context context = this; // Reference to your Android Activity.
    new PaymentSDK(context, options).validateCard(request, new IswCallback<ValidateCardResponse>() { 
            //Send payment
            @Override
            public void onError(Exception error) {
                // Handle error and notify the user.
                // Payment not successful.
            }

            @Override
            public void onSuccess(ValidateCardResponse response) {
                // Check if OTP is required.
                if (StringUtils.hasText(response.getResponseCode())) {                
                   if (PaymentSDK.SAFE_TOKEN_RESPONSE_CODE.equals(response.getResponseCode())) {
                        // OTP required, ask user for OTP and authorize transaction
                        // See how to authorize transaction with OTP below.
                   }
                   else if (PaymentSDK.CARDINAL_RESPONSE_CODE.equals(response.getResponseCode())) {
                        // redirect user to cardinal authorization page
                        // See how to authorize transaction with Cardinal below.
                   }                   
                }
                else {
                     // OTP not required.
                     // Handle and notify user of successful transaction. 
                     // A token for the card details is returned in the response.
                }
                // The response object contains fields transactionIdentifier, 
                // message,token, tokenExpiryDate, panLast4Digits, otpTransactionIdentifier
                // transactionRef and cardType. 
                // Save the token, tokenExpiryDate, cardType and panLast4Digits 
                // in order to pay with the token in the future.
            }
    });
```

## <a name='AuthorizeOTP'></a>Authorize PayWithCard using OTP
```java 
if (StringUtils.hasText(response.getResponseCode())) { // 
    if (PaymentSDK.SAFE_TOKEN_RESPONSE_CODE.equals(response.getResponseCode())) {
        AuthorizePurchaseRequest request = new AuthorizePurchaseRequest();
        request.setPaymentId(response.getPaymentId()); // Set the payment identifier for the request
        request.setAuthData(request.getAuthData()); // Set the request Auth Data
        request.setOtp("123456"); // Accept OTP from user
         new PaymentSDK(context, options)
         .authorizePurchase(request, new IswCallback<AuthorizePurchaseResponse>() {
            @Override
            public void onError(Exception error) {
                // Handle and notify user of error
            }
            @Override
            public void onSuccess(AuthorizePurchaseResponse otpResponse) {
                 //Handle and notify user of successful transaction
            }
        });
    }
    if (PaymentSDK.CARDINAL_RESPONSE_CODE.equals(response.getResponseCode())) {
        // Create WebView to process the Authorize purchase request
        webView = new AuthorizeWebView(context, response) {
            @Override
            public void onPageDone() {                    
                AuthorizePurchaseRequest request = new AuthorizePurchaseRequest();
                request.setAuthData(request.getAuthData()); // Set the request Auth Data.
                request.setPaymentId(response.getPaymentId()); // Set the payment identifier for the request.
                request.setTransactionId(response.getTransactionId()); // Set payment identifier for the request.
                request.setEciFlag(response.getEciFlag());   // Set the Electronic Commerce Indicator (ECI).
                new PaymentSDK(context, options)
                .authorizePurchase(request, new IswCallback<AuthorizePurchaseResponse>() {
                    @Override
                    public void onError(Exception error) {
                        // Handle and notify user of error
                    }
                    @Override
                    public void onSuccess(AuthorizePurchaseResponse response) {
                        //Handle and notify user of successful transaction
                    }
                });
            }
            @Override
            public void onPageError(Exception error) {
                // Handle and notify user of error
            }
        };
        // Other webview customizations goes here e.g.
        webView.requestFocus(View.FOCUS_DOWN);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setVerticalScrollBarEnabled(true);
    }
}
   
```

## <a name='ValidateCardOTP'></a>Authorize Card Validation using OTP
```java 
if (StringUtils.hasText(response.getResponseCode())) { // 
    if (PaymentSDK.SAFE_TOKEN_RESPONSE_CODE.equals(response.getResponseCode())) {
        AuthorizeCardRequest request = new AuthorizeCardRequest();
        request.setTransactionRef(response.getTransactionRef()); // Set the transaction reference for the request
        request.setAuthData(request.getAuthData()); // Set the request Auth Data
        request.setOtp("123456"); // Accept OTP from user
         new PaymentSDK(context, options)
         .authorizeCard(request, new IswCallback<AuthorizeCardResponse>() {
            @Override
            public void onError(Exception error) {
                // Handle and notify user of error
            }
            @Override
            public void onSuccess(AuthorizeCardResponse authorizeCardResponse) {
                 //Handle and notify user of successful transaction
            }
        });
    }
    if (PaymentSDK.CARDINAL_RESPONSE_CODE.equals(response.getResponseCode())) {
        // Create WebView to process the Authorize purchase request
        webView = new AuthorizeWebView(context, response) {
            @Override
            public void onPageDone() {                    
                AuthorizeCardRequest request = new AuthorizeCardRequest();
                request.setAuthData(request.getAuthData()); // Set the request Auth Data.
                request.setPaymentId(response.getPaymentId()); // Set the payment identifier for the request.
                request.setTransactionId(response.getTransactionId()); // Set payment identifier for the request.
                request.setEciFlag(response.getEciFlag());   // Set the Electronic Commerce Indicator (ECI).
                new PaymentSDK(context, options)
                .authorizeCard(request, new IswCallback<AuthorizeCardResponse>() {
                    @Override
                    public void onError(Exception error) {
                        // Handle and notify user of error
                    }
                    @Override
                    public void onSuccess(AuthorizeCardResponse response) {
                        //Handle and notify user of successful transaction
                    }
                });
            }
            @Override
            public void onPageError(Exception error) {
                // Handle and notify user of error
            }
        };
        // Other webview customizations goes here e.g.
        webView.requestFocus(View.FOCUS_DOWN);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setVerticalScrollBarEnabled(true);
    }
}
   
```

 
## <a name='AuthorizeWOTP'></a>Authorize PayWithWallet using OTP

```java
	if (StringUtils.hasText(response.getOtpTransactionIdentifier())) { // 
            AuthorizeOtpRequest otpRequest = new AuthorizeOtpRequest(); 
            // Setup request parameters using the selected Payment Method
            otpRequest.setOtp("123456"); // Accept OTP from user
            // Set the OTP identifier for the request
            otpRequest.setOtpTransactionIdentifier(response.getOtpTransactionIdentifier()); 
             // Set the unique transaction reference.
            otpRequest.setTransactionRef(response.getTransactionRef());
            //Authorize OTP Request
            AuthorizeOtpResponse otpResponse = new PurchaseClient(options).authorizeOtp(otpRequest);  
            //Handle and notify user of successful transaction               
    }
```

 
### <a name='PaymentStatus'></a>Checking Payment Status

To check the status of a payment made, use the code below

```java
	//Pass the transactionRef and the amount as the parameters to getPaymentStatus()
	PaymentStatusRequest request = new PaymentStatusRequest();	 
	request.setTransactionRef("117499114589");
	request.setAmount("100");
	new PaymentSDK(context, options).getPaymentStatus(request, new IswCallback<PaymentStatusResponse>() {
            @Override
            public void onError(Exception error) {
                // Handle and notify user of error
            }

            @Override
            public void onSuccess(PaymentStatusResponse response) {
                // Update Payment Status
            }
    });
```


## <a name='BlackBerry'></a>Using Android SDK to Create Blackberry Application
To create a Blackberry app using the **runtime for Android** 

1. Create an android app as above using SDK provided for android
2. Convert the app according to the instructions stated on Blackberry's website [here] (http://developer.blackberry.com/android/) and [here] (http://developer.blackberry.com/android/documentation/bb_android_studio_plugin_tool.html)
