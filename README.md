# README #

## Steps to setup webhook in Stripe ##
We are writing the steps for setting up service endpoint deployed on localhost, you just
 need to replace ngrok URL with actual server URL when going to production

### Download and start ngrok ###
* Download and install ngrok https://ngrok.com
* Start ngrok using the command ./ngrok http 8080, you can change the port as needed
* Note down secure URL provided by ngrok

### Steps to setup webhook in Stripe ###

* Login to stripe dashboard https://dashboard.stripe.com
* Open Developers>Webhooks menu
* Click on Add endpoint against the option "Endpoints receiving events from your account"
* Enter endpoint URL, copied from previous step by appending /payment-service/stripe-events e;g https://cb5a17f34c0e.ngrok.io/payment-service/stripe-events
* Select the events you are interested from "Event to send" dropdown
we have provided implementation for the following events so far
  * payment_method.attached
   * payment_intent.succeeded
   * customer.created
   * checkout.session.completed

### Run payment service application ###
First Update the property service.url with ngrok or prod server endpoint in application-*.properties file, * based on application profile
e;g service.url=https://cb5a17f34c0e.ngrok.io

Run payment service "PaymentServicesApiWebApplication", by default this SpringBoot application will run on port 8080, since we had
started ngrok to listen to port 8080, that will forward traffic to our application on port 8080.


### Testing the webhook locally ###
Install and run stripe cli, following the instrusctions in following page
https://stripe.com/docs/webhooks/test

* Install Stripe
* run command, stripe login
* Forward events to your server
  * stripe listen --forward-to https://3fd7c5b45605.ngrok.io/payment-service/stripe-events
  * above command will start listening to further commands
  * for excample run command in another window: stripe trigger payment_intent.succeeded 