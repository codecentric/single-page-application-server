#!/bin/sh -ex

sudo apt -yq update
sudo apt -yq install make curl

sudo curl -sL https://github.com/hairyhenderson/gomplate/releases/download/v3.6.0/gomplate_linux-amd64-slim -o /usr/local/bin/gomplate
sudo chmod 755 /usr/local/bin/gomplate
