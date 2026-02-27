$ErrorActionPreference = "Stop"

$topics = @(
    "settlement.adjustment.v1",
    "settlement.adjustment.retry.5m.v1",
    "settlement.adjustment.dlq.v1",
    "settlement.completed.v1"
)

foreach ($topic in $topics) {
    docker exec ksp-kafka kafka-topics.sh `
        --bootstrap-server localhost:9092 `
        --create `
        --if-not-exists `
        --topic $topic `
        --partitions 3 `
        --replication-factor 1
}

Write-Host "토픽 준비 완료."
