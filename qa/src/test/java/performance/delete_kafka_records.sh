#!/bin/bash

set -e

BOOTSTRAP_SERVER="kafka:9071"
TOPIC="fx_rates"

echo "Getting all kafka topics"

# extracting partitions numbers..
PARTITIONS=$(kafka-topics --bootstrap-server "$BOOTSTRAP_SERVER" --describe --topic "$TOPIC" | awk -F 'Partition: ' '/Partition:/ {print $2}' | awk '{print $1}')

if [ -z "$PARTITIONS" ]; then
    echo "can't get number partition of $TOPIC"
    exit 1
fi

# creating temp file
DELETE_RECORDS_JSON="/tmp/delete_records.json"
echo "{
  \"partitions\": [" > "$DELETE_RECORDS_JSON"

first=true
for partition in $PARTITIONS; do
    if [ "$first" = true ]; then
        first=false
    else
        echo "," >> "$DELETE_RECORDS_JSON"
    fi
    echo "    { \"topic\": \"$TOPIC\", \"partition\": $partition, \"offset\": -1 }" >> "$DELETE_RECORDS_JSON"
done

echo "  ], \"version\": 1 }" >> "$DELETE_RECORDS_JSON"

echo "delete all events from $TOPIC..."
cat "$DELETE_RECORDS_JSON"
kafka-delete-records --bootstrap-server "$BOOTSTRAP_SERVER" --offset-json-file "$DELETE_RECORDS_JSON"


rm -f "$DELETE_RECORDS_JSON"

echo "done!"