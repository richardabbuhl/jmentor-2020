#!/bin/bash
grep "^1\." results.txt | grep Random | awk '{print $1,$9,$2,$3}' | sort | tail
grep "^2\." results.txt | grep Random | awk '{print $1,$11,$2,$3}' | sort | tail