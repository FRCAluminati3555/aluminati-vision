#!/bin/sh

sudo cp ../services/aluminativision.service /etc/systemd/system
sudo systemctl enable aluminativision.service
