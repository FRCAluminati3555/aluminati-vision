#!/bin/sh

sudo cp ../services/booster.service /etc/systemd/system
sudo systemctl enable booster.service
