#!/bin/bash

mkdir -p /home/$USER/Pictures/Wallpapers

/bin/cp -rf ./images/newimage.$1 /home/$USER/Pictures/Wallpapers/background.$1

gsettings set org.gnome.desktop.background picture-uri "file:///home/$USER/Pictures/Wallpapers/background.$1"

echo done 
