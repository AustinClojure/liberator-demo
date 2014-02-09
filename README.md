# liberator-demo

Liberator demo for Austin Clojure Meetup

# running

## to create the database:

`lein make-db`

## to run

`lein ring server-headless`

## to list users

`lein users`


## Example usage

# Get games for a user with apikey (you can get a users apikey via the 'lein users' above)
http://localhost:3000/api/games?apikey=420f4274-08af-4fa6-bd29-6ccf259a5390
