# isw-payment-sdk-android

## Payment SDK for Android

Interswitch payment SDK allows you to accept payments from customers within your mobile application.
The first step to ​using the ​Android SDK is to register as a merchant. This is described [here] (http://merchantxuat.interswitchng.com/paymentgateway/getting-started/overview/sign-up-as-a-merchant)


### Download the SDK

Download the SDK from the link below

https://github.com/techquest/isw-payment-sdk-android/releases

It consists of ​4 libraries:

1. *core.jar*
2. *payment.jar*
3. *payment-android-release.aar*
4. *gson-2.​3.1.jar*

**Use Android Studio’s dependency management tool (Gradle) to add the libraries to your project.**


### Creating the project and adding libraries using Android Studio

1. Download **Android Studio 1.2.2** or later
2. Create a new project
3. Add **core.jar, payment.jar, payment-android-release.aar** and **gson-2.​3.1.jar** to your project by putting them in the libs folder of the app.
4. To add the **payment-android-release.aar** library, navigate to **File -> New -> New Module -> Import .JAR/.AAR Package** option in Android Studio.
5. Select the **payment-android-release.aar**
6. Finally, rebuild the project 

### Accepting Payments with Card 

Ask the user for input

In the onClick method of the button that asks the user to pay, add this code

```java
//Pay with Card
    RequestOptions options = RequestOptions.builder()
	.setClientId("IKIA3E267D5C80A52167A581BBA04980CA64E7B2E70E")
	.setClientSecret("SagfgnYsmvAdmFuR24sKzMg7HWPmeh67phDNIiZxpIY=").build(); 
```
```java
	final PurchaseRequest request = new PurchaseRequest();
    //Optional email, mobile no, BVN etc to uniquely identify the customer
	request.setCustomerId(“1234567890"); 
    request.setAmount(“100"); //Amount in Naira
    request.setPan(“5060100000000000012"); //Card No
    request.setPinData("1111"); //Card PIN
    request.setExpiryDate("2004"); // expiry date in YYMM format
    request.setCurrency("NGN");
    request.setTransactionRef(RandomString.numeric(12)); //unique transaction reference
    Context context = this; // reference to your Android Activity
```
```java	
    //Send payment
    new PaymentSDK(context, options).purchase(request, new IswCallback<PurchaseResponse>() {
    @Override
    public void onError(Exception error) {
        // Handle and notify user of error
    }
    @Override
    public void onSuccess(PurchaseResponse response) {
        if (StringUtils.hasText(response.getOtpTransactionIdentifier())) {
           //OTP required
           //Ask user for OTP and authorize transaction using the otpTransactionIdentifier
        } 
		else { 
         //OTP not required
         //Handle and notify user of successful transaction
        }
    }
    });
	
```

## Accepting Payment with Wallet


To load Verve wallet, add this code
```java
    //Load Wallet
    final WalletRequest request = new WalletRequest();
    request.setTransactionRef(RandomString.numeric(12));
    Context context = this; // reference to your Android Activity
    new WalletSDK(context, options).getPaymentMethods(request, new IswCallback<WalletResponse>() {
    @Override
    public void onError(Exception error) {
        // Handle and notify user of error
    }
 
    @Override
    public void onSuccess(WalletResponse response) {
		PaymentMethod[] paymentMethods = response.getPaymentMethods(); 
		//Display payment methods in a Spinner
    }
	});
​
```
After populating the spinner, when the user clicks an item and the clicks pay, use this code
```java
    //Pay with Wallet Item
    final PurchaseRequest request = new PurchaseRequest();	
```
```java
    //Optional email, mobile no, BVN etc to uniquely identify the customer
	request.setCustomerId(“1234567890"); //Amount in Naira
    request.setAmount("100"); 
    if (paymethodSpinner.getSelectedItem() == null) {
		return;
    }
```
```java
    request.setPan(((PaymentMethod) paymethodSpinner.getSelectedItem()).getToken());
    request.setPinData(pin.getText().toString());
    request.setCurrency("NGN");
    request.setTransactionRef(RandomString.numeric(12));
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
        } else { //OTP not required
           //Handle and notify user of successful transaction
        }
    }
	
```