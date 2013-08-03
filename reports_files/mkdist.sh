
#!/bin/bash
OUTZIPFILE=${HOME}/java/ADA/assets/defaultreports.zip
DEVDIRS="Everything"
TMPDIR=/tmp/mkdist$$

mkdir ${TMPDIR}
tar --exclude="*.svn*" --exclude="*~" -cf - . | ( cd ${TMPDIR} ; tar -xvf -)

rm ${OUTZIPFILE}
cd ${TMPDIR}
zip -r ${OUTZIPFILE} ./*
zip -d ${OUTZIPFILE} $0
if [ ${1:-NOTHING} != "dev" ]
then
    for kk in ${DEVDIRS}
    do
	zip -d ${OUTZIPFILE} ${kk}'/*'
    done
fi
