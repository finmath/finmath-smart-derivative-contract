# This is about the features that requires server side storage. The proper README is the other one.

## What is it that requires server side storage?

Saving/loading preliminary contracts and uploading own market data. The implementation for both these features is to be
considered experimental.

## How do I tell the server application where to save stuff?

Edit `storage.basedir` in `src/main/resources/application.yml`. Spring is very nitpicky when it comes to accessing the
disk, so an absolute path is needed.

## What does the folder structure used by the application mean?

Folders follow the `username.foldername` naming convention. For now naming is hardcoded as if `user1` is the only one
existing. This is not a definitive solution: if usage of the filesystem as a database is the definitive choice, small
modifications to the `PlainSwapEditorController` are needed. Also, the server should care a bit better about file
ownership. If instead this solution is to be scrapped in favor of a full DBMS approach, this does not apply.

## Why aren't the contract templates and schemas handled in the same way?

Every template more or less corresponds to a standard contract. There aren't that many, they can be considered as
program resources. Nonetheless, using disk storage instead of program resources for this is not a big change.

## Why is there just one file for market data?

It's a rolling file. Market data changes rapidly, so storing the old data might not be too useful (for now). Still,
usage of the same framework that is in place for the preliminary contracts allows this to be easily changed later.

## What is the format for market data?

It's a very, very, very non-standard JSON. See `md_testset2.json`. We won't make the tools for generation of those files
public, lest we face the wrath of the lawyers.


