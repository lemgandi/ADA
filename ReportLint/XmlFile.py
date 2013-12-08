#!/usr/bin/env python

import sys
import os.path
import xml.dom.minidom as xml
import re
import string

class XmlOption:
    "Base class for an xml option to be linted "
    def __init__(self,name,isOptional=True):
        self.name=name
        self.isOptional=isOptional
        self.isValid = True  # Ever optimistic!

class XmlFile:
    "Base class for xml file linter"
    def __init__(self,dir):
	"""Root object initialization"""
        self.inDir=dir
        if (False == self.inDir.endswith(os.path.sep)):
            self.inDir = self.inDir+os.path.sep
	self.inputNames = []
        self.errors=[]
        self.isValid=True
        self.inFileName=''

    def addErrorString(self,thestr):
	"""Add an error to the list of troubles"""
        self.errors.append(thestr)

    def getErrors(self):
	"""Return troubles with this file"""
        return self.errors

    def getIsValid(self):
	"""Is this object valid? (Did it pass all checks?)"""
        return self.isValid

    def getInputFileName(self):
	"""Get input filename built from inDir + descended object filename"""
        return self.inFileName
