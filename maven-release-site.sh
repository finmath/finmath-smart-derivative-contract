
# deploy site (clover:instrument takes a long time)
find . -name ".DS_Store" -type f -delete
mvn clean install site site:stage site-deploy
