# Linelos

## What this program does

_Linelos_ is an api made in Clojure that uses Google
[Gmail API](https://developers.google.com/gmail/api/) to search for the user
emails, then it extracts data using regexes and returns a json object.

It also takes advantage of Clojure's interop with Java to use the
[gmail java library](https://developers.google.com/gmail/api/quickstart/java).
So in the end you take the compatibility of Java with the functional programming
goodness of Clojure.

### An Example

Supose you have a bunch of emails from your bank with a similar format:

```
From:notifications@thebank.com
===============================

Hello Roberto,

You have made a payment with your account # 123

Vendor: Some vendor
Date of transaction 05/02/2019 at 16:14
Amount $ 1.00.
```

You can send Linelos a request with params similar to what you would type in a
search inside your gmail client

```bash
curl -XGET localhost:3478/transactions?query=from:(notifications@thebank.com)%20payment
```

The [message parser](src/linelos/gmail/service.clj#L22) will process each email
that matches the query and extract the fields according to the
[provided regexes](src/linelos/gmail/message/pacificard.clj). You will end with
a response that looks like this:

```json
{
  "transacciones": [
    {
      "vendor": "Some vendor",
      "date": "2019-02-05T16:14:00Z",
      "amount": 1
    },
    {
      "vendor": "Some other vendor",
      "date": "2019-03-01T11:11:00Z",
      "amount": 9001
    }
  ]
}
```

## What's Next

* Currently, only [one format](src/linelos/gmail/message/pacificard.clj) of
  message is supported. That is just the basic idea put into workable code, but
  ideally the parsing and regex stuff would be provided with the request.
* Document the API

## Setup

* [Turn on the Gmail API](https://developers.google.com/gmail/api/quickstart/java#step_1_turn_on_the_api_name)
* Copy your `client_secret.json` into the root directory of this project
* Configure the local environment:
  * Rename `.lein-env.example` to `.lein-env` and edit it accordingly
* Run `lein ring server-headless` to start the local server

## Local Development

| Command                            | Description                                         |
| ---------------------------------- | --------------------------------------------------- |
| `lein ring server-headless [port]` | Start server locally                                |
| `lein test:unit`                   | Run unit tests                                      |
| `lein test:functional`             | Run functional tests                                |
| `lein test`                        | Run all tests                                       |
| `lein test:coverage`               | Run tests with code coverage report                 |
| `lein kibit`                       | Analyze code for more idiomatic functions or macros |
| `lein cljfmt fix`                  | Run linter and fix files                            |
| `lein check`                       | Run all checks and tests                            |
