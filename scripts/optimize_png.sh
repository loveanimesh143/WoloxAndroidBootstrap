#!/bin/bash
IFS=$(echo -en "\n\b")

if [ $# -lt 1 ]
then
	echo "Missing argument"
	echo "Usage : $0 <APPLICATION_PROJECT_DIRECTORY>"
	exit 1;
fi
APPLICATION_PROJECT_DIRECTORY=$1

echo "Starting PNGs assets optimization using PNGQuant library"
let "optimizedCount = 0"
for png in $(find "$APPLICATION_PROJECT_DIRECTORY" -iname "*[^9].png")
do
	is_optimized=$(exiftool "$png" | grep -e "Color Type.*:.*Palette")
	if [ -z "$is_optimized" ]
	then
		already_optimized=$(pngquant "$png" --ext .png --force 2> last_error)
		if [ $? -eq 0 ]
		then
			echo "Optimized - $png"
			let "optimizedCount++"
		else
			echo "Optimization failed for : $png - Reason : $(cat last_error)" >&2
		fi
	fi
done
rm last_error 2> /dev/null
echo "Optimized $optimizedCount PNGs assets"
