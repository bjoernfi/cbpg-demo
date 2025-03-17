#!/bin/bash

source .env
mkdir -p $LDAP_DIR && chmod 777 $LDAP_DIR -R
mkdir -p $KC_DIR && chmod 777 $KC_DIR -R