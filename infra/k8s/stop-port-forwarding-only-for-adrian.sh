#!/bin/bash
set -e

ps aux | grep -i "kubectl port-forward"  | grep -i "9094" | grep -v grep | awk {'print $2'} | xargs kill
ps aux | grep -i "kubectl port-forward"  | grep -i "8082" | grep -v grep | awk {'print $2'} | xargs kill
ps aux | grep -i "kubectl port-forward"  | grep -i "3082" | grep -v grep | awk {'print $2'} | xargs kill
ps aux | grep -i "kubectl port-forward"  | grep -i "3083" | grep -v grep | awk {'print $2'} | xargs kill
ps aux | grep -i "kubectl port-forward"  | grep -i "4081" | grep -v grep | awk {'print $2'} | xargs kill
