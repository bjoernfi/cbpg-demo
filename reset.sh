#!/bin/bash

source .env

echo "Purging"
sudo rm -rf $LDAP_DIR
sudo rm -rf $KC_DIR
sudo rm -rf $KC_DB_DIR
sudo rm -rf $MONGO_DIR
./setup.sh