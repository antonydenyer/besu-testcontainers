# Testing Ethereum Networks with Testcontainers

The following code will create a 3 node besu network with 1 miner and 2 full nodes. 
The network is configured with docker-compose and was inspired by https://docs.goquorum.consensys.net/en/stable/Tutorials/Quorum-Dev-Quickstart/

## Running

`./gradle check` will run the test

The test simply deploys a smart contract into a private dev network then sets a value. This could be used as a template or inspiration for further testing.

## Blog post

https://www.antonydenyer.co.uk/2021-09-09-running-a-besu-network-with-test-containers/
