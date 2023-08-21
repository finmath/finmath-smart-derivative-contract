# This is about setting up a PostgreSQL database for use by the valuation service and enabling the Livefeed. Refer to the other READMEs if you need anything else.

## What needs to be done before deployment?

Go to `application.yml` and set the following parameters:

- `storage.internals.refinitivConnectionPropertiesFile` should point to the connection properties file for a Refinitiv
  ELEKTRON live feed.
- `storage.internals.databaseConnectionPropertiesFile` should point to the connection properties file to the database.
  You can find a sample in `src/main/resources/sql`.
- `storage.importdir` should point to a folder where the database user has `rw-` permissions (for POSIX servers), or to
  the public user folder (for Windows servers).

Using the tool that you prefer (either PgAdmin or the PSQL console), run `src/main/resources/sql/build_db.sql`. It
should just work, if it doesn't check permissions and database defaults that may prevent this.

There is a possibility that with an empty database the valuation server might fail when using the Livefeed. To fill the
database with some initial records, use the frontend application to load a sample dataset into the server.
Whenever the user uploads a dataset through the UI, a copy of the dataset is automatically merged in the database. If
you installed the service correctly, every user should have a non-empty active dataset and this operation should be
possible.
Remember that if the valuation service has an empty active dataset, its state is undetermined and everything will most
likely cascade-crash.

## Possible issues at runtime

- *The Livefeed won't connect at all:* check the proxy configs. It is also possible that if the application crashes at a
  very unfortunate time, the connection auto-reset routine won't run properly and the Refinitiv server is left hanging
  waiting for the valuation service to acknowledge reception of the data. In this case, wait 15 minutes before
  restarting the service. This, however, should be exceptionally rare and has never been seen in testing.
- *The database won't connect at all and/or Spring Boot fails to load ApplicationContext because of
  corrupted `databaseConnector` bean*: double-check the configs. Otherwise, there's nothing you can do. Report the issue
  to the current mantainer (as of July
  7, Luca).
- *The dataset hotswap won't work and/or the import routine fails:* make sure that the database user has all necessary
  permission to operate on the import folder / import file.
