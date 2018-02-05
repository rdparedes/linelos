# Linelos

This app uses Google's [Gmail API](https://developers.google.com/gmail/api/) to
fetch your data. It also uses the
[Java library](https://developers.google.com/gmail/api/quickstart/java) and
takes advantage of Clojure's Java interop.

## Setup

* [Turn on the Gmail API](https://developers.google.com/gmail/api/quickstart/java#step_1_turn_on_the_api_name)
* Copy your `client_secret.json` into the root directory of this project
* Configure the local environment:
  * Rename `.lein-env.example` to `.lein-env` and edit it accordingly
* Run `lein ring server-headless` to start the local server

## Development

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

## TODO:

* Make a frontend
* Make the message parser smarter
* Document API
