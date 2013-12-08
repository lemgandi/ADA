#!/usr/bin/env python

import sys
import os.path
import xml.dom.minidom as xml
import re
import string
from optparse import OptionParser

import XmlFile
import InputXmlFile as InputXmlFile
import QueryXmlFile as QueryXmlFile
import OutputXmlFile as OutputXmlFile


cmdLineParser=OptionParser()
cmdLineParser.add_option('-r','--report',dest="reportname",
help="Report to lint")
cmdLineParser.add_option('-v','--verbose',action='store_true',dest="verbose",default=False,
help="Verbosity")

(options,args)=cmdLineParser.parse_args()

verbose=options.verbose
thedir = options.reportname
Success=True

if None == thedir:
    print("Oops.  Please supply the name of the report you wish to lint.")
    Success = False

if not os.path.isdir(thedir):
    print("Oops. [%s] not found." % (thedir))
    Succcess = False

if( True == Success):

    if thedir.endswith(os.path.sep):
        thedir=thedir[:(len(thedir)-1)]

    Input = InputXmlFile.InputXmlFile(thedir)
    isValid = Input.validateChildren()

    if True == isValid:
        print('%s is valid' % (Input.getInputFileName()))
    else:
        print('%s is not valid' % (Input.getInputFileName()))
        for kk in Input.getErrors():
            print('Error: %s' % (kk))

    inputParms=Input.getInputNames()
    if(True == verbose):
        print("Input Names:")
        for kk in inputParms:
            print("Name: " + kk)

    Query = QueryXmlFile.QueryXmlFile(thedir)
    isValid = Query.validateChildren()
    if True == isValid:
        print('%s is valid' % (Query.getInputFileName()))
    else:
        print('%s is not valid' % (Query.getInputFileName()))
        for kk in Query.getErrors():
            print('Error: %s' % (kk))

    queryParms=Query.getParamsFound()

    if(True == verbose):
        print("Query Names:")
        for kk in queryParms:
            print("Name: " + kk)

    Output=OutputXmlFile.OutputXmlFile(thedir)
    isValid=Output.validateChildren()
    if True == isValid:
        print('%s is valid' % (Output.getInputFileName()))
    else:
        print('%s is not valid' % (Output.getInputFileName()))
        for kk in Output.getErrors():
            print('Error: %s' % (kk))

    outputParms=Output.getParamsFound()
    if (True == verbose):
        print("Output Names:")
        for kk in outputParms:
            print("Name: " + kk)

    for kk in inputParms:
        if kk not in queryParms:
            print("Dangling input parameter: %s (not in query)" % (kk))
    for kk in queryParms:
        if kk not in inputParms:
            print("Dangling query parameter: %s (not in input)" % (kk))
    for kk in outputParms:
        if kk not in inputParms:
            print("Dangling output parameter: %s (not in input)" % (kk))
