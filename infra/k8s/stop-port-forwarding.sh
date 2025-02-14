#!/bin/bash
set -e
ps -ef | grep kubectl | grep port || echo "Before: No active port forwarding processes ."

ps aux | grep -i "kubectl port-forward"  | grep -i "9093" | grep -v grep | awk {'print $2'} | xargs --no-run-if-empty kill || true 
ps aux | grep -i "kubectl port-forward"  | grep -i "8081" | grep -v grep | awk {'print $2'} | xargs --no-run-if-empty kill || true
ps aux | grep -i "kubectl port-forward"  | grep -i "3080" | grep -v grep | awk {'print $2'} | xargs --no-run-if-empty kill || true
ps aux | grep -i "kubectl port-forward"  | grep -i "4080" | grep -v grep | awk {'print $2'} | xargs --no-run-if-empty kill || true
ps aux | grep -i "kubectl port-forward"  | grep -i "4081" | grep -v grep | awk {'print $2'} | xargs --no-run-if-empty kill || true

echo "Port forwarrding stopped"