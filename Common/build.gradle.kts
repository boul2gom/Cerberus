dependencies {
    api("org.apache.logging.log4j:log4j-slf4j2-impl:2.20.0")
    api("org.apache.logging.log4j:log4j-core:2.20.0")

    api("io.lettuce:lettuce-core:6.2.4.RELEASE")
    api("com.rabbitmq:amqp-client:5.17.0")

    api("org.mongodb:mongodb-driver-sync:4.10.0-alpha0")

    api("net.spy:spymemcached:2.12.3")

    api("io.github.nullptr:tools-docker:1.3.4")

    api(project(":API"))
}