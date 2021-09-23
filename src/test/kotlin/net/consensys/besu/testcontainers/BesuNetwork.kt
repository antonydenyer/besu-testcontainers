package net.consensys.besu.testcontainers

import java.io.File
import org.slf4j.LoggerFactory
import org.testcontainers.containers.DockerComposeContainer
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.shaded.com.google.common.io.Resources.getResource
import org.web3j.crypto.Credentials
import org.web3j.protocol.core.JsonRpc2_0Web3j
import org.web3j.protocol.http.HttpService
import org.web3j.utils.Async

class BesuNetwork {
    private val logger = LoggerFactory.getLogger(BesuNetwork::class.java)

    private val nodes: Map<String, BesuNode>

    private val instance: KDockerComposeContainer by lazy { KDockerComposeContainer(File("src/test/resources/docker-compose.yml")) }

    class KDockerComposeContainer(file: File) : DockerComposeContainer<KDockerComposeContainer>(file)

    init {
        instance.withEnv("BESU_VERSION", System.getenv("BESU_VERSION"))

        val serviceNames = listOf("miner", "alice", "bob")

            serviceNames
            .forEach {
                val serviceName = "${it}_1"
                instance
                    .withExposedService(serviceName, 8545, Wait.forListeningPort())
                    .waitingFor(serviceName, Wait.forLogMessage(".*Ethereum main loop is up.*", 1))
                    .withLogConsumer(
                        serviceName,
                        Slf4jLogConsumer(logger).withPrefix(it)
                    )
            }

        instance.start()

        nodes = serviceNames
            .associateWith {
                val rpcUrl =
                    "http://${instance.getServiceHost("${it}_1", 8545)}:${instance.getServicePort("${it}_1", 8545)}"
                val web3j = JsonRpc2_0Web3j(HttpService(rpcUrl), 2000, Async.defaultExecutorService())
                val credentials =
                    Credentials.create(getResource(BesuNetwork::class.java, "/config/$it/key").readText())

                BesuNode(
                    web3j,
                    credentials
                )
            }
    }

    fun stop() = instance.stop()
    fun get(key: String) = nodes[key]!!
}
