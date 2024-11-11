#!/bin/sh

var=$(cat VERSION)
IFS=. read -r version minor patch <<EOF
$var
EOF

case "$1" in
major)     tag="$((version+1)).0.0"; ;;
minor)     tag="$version.$((minor+1)).0"; ;;
patch)     tag="$version.$minor.$((patch+1))"; ;;
*)         echo "Specify: major, minor, patch"; exit 1; ;;
esac

echo "$(printf "%s" "$var") -> $(printf "%s" "$tag")"
printf "%s" "$tag" > VERSION
