#!/bin/bash

declare -a targets

targets=(
)

while true; do
  for i in ${targets[@]}; do
    echo "Target environment: ${i}"

    echo "Fetching user list:"
    # Get ECommerce application URL
    END_POINT_URL=`grep END_POINT_URL PreferenceConstants.${i}`
    BASE_URL=`echo $END_POINT_URL | awk '{ print $7}'`
    # Reduce to base URL
    BASE_URL1=`echo ${BASE_URL#\"}`
    BASE_URL2=`echo ${BASE_URL1%/\";}`
    echo "Using: $BASE_URL2/rest/json/user/all"

    # Copy to Android test project assets folder
    mkdir -p app/src/androidTest/assets
    response=$(curl --write-out "%{http_code}\n" --silent --output app/src/androidTest/assets/users.json ${BASE_URL2}/rest/json/user/all)
    echo "Response: $response"
    if [ $response -ne "200" ]; then
      echo "Error: unable to retrieve user list"
      break
    fi

    # Copy Preference Constants to project src tree
    cp PreferenceConstants.${i} app/src/main/java/com/appdynamics/pmdemoapps/android/ECommerceAndroid/misc/PreferenceConstants.java 

    # Wake up the device(s) and run
    ./WakeUpAndRun.sh
  done
done
