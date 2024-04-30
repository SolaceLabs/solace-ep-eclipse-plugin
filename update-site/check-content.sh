#!/bin/sh

# https://solacedotcom.slack.com/archives/G017BUW0U1K/p1709706349823239

# This utility script is meant to check if the "category" feature is working correctly.  Run this script against your newly generated Update Site.
# If you see some output, then it is configured correctly.
# If no output, then the "category" did not get exported properly.  When people try to install the plugin, it will not show up in a category
# and people will have to click on "view uncategorized items" to see it.

echo Should see something here if configured correctly.
jar xf content.jar
grep EventDriven content.xml
rm content.xml

