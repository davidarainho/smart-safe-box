#!/bin/bash

pip install -r requirements.txt

python3 server_esp_api.py &
# python3 appConnectLayer.py

wait