package net.consensys.besu.testcontainers

import org.web3j.crypto.Credentials
import org.web3j.protocol.core.JsonRpc2_0Web3j

data class BesuNode(val web3j: JsonRpc2_0Web3j, val credentials: Credentials)