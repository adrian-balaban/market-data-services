#!/bin/bash
set -e

ps aux | grep -i "kubectl port-forward"  | grep -i "9093" | grep -v grep | awk {'print $2'} | xargs kill
ps aux | grep -i "kubectl port-forward"  | grep -i "8081" | grep -v grep | awk {'print $2'} | xargs kill
ps aux | grep -i "kubectl port-forward"  | grep -i "3080" | grep -v grep | awk {'print $2'} | xargs kill
ps aux | grep -i "kubectl port-forward"  | grep -i "4080" | grep -v grep | awk {'print $2'} | xargs kill
