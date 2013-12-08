#!/usr/bin/env python

import sys
import os.path
import xml.dom.minidom as xml
import re
import string

from XmlFile import XmlFile
from XmlFile import XmlOption

class InputXmlFile(XmlFile):
    """Read and parse Input.xml"""
    def __init__(self,dir):
        """Init: dir = directory containing Input.xml"""
        XmlFile.__init__(self,dir)
        self.ValidFieldNames=['Label','InputField','#text']
        self.ValidInputOptions={
            'Label':[],
            'Fencer':[XmlOption('height'),XmlOption('position'),XmlOption('seq',False)],
            'Action':[XmlOption('height'),XmlOption('position'),XmlOption('seq',False)],
            'Integer':[XmlOption('height'),XmlOption('position'),
                       XmlOption('limit'),XmlOption('seq',False)],
            'Boolean':[XmlOption('height'),XmlOption('position'),XmlOption('seq',False)],
            'Interval':[XmlOption('height'),XmlOption('position'),
                        XmlOption('maxMeasure',False),XmlOption('seq',False)],
            'Id':[XmlOption('height'),XmlOption('position'),
                  XmlOption('table',False),XmlOption('seq',False)],
            'Text':[XmlOption('height'),XmlOption('position'),
                    XmlOption('quotes'),XmlOption('default'),XmlOption('seq',False)]
            }
        self.ValidLabelTypes=['Header']
        self.inFileName=self.inDir+"Input.xml"
        self.MyDom = xml.parse(self.inFileName)
        self.inputNames=[]

    def validateInputField(self,node,lno):
        """Validate input field node at lno"""
        retVal=True
        if node.getAttribute('type') not in self.ValidInputOptions.keys():
            self.addErrorString='Invalid input field type: %s at %d' % (k.getAttribute('type'),lno)
            retVal=False

        if '' == node.getAttribute('label'):
            retVal=False
            self.addErrorString('Input field lacks label at %d' % (lno))

        if '' == node.getAttribute('name'):
            retVal=False
            self.addErrorString('Input field lacks name at %d' % (lno))
        else:
            self.inputNames.append(node.getAttribute('name'))
        if retVal == True:
            self.checkAttributes(node.attributes,self.ValidInputOptions[node.getAttribute('type')],lno)
        return retVal
    
    def checkAttributes(self,theAttrs,validAttrs,lno):
        """Validate node attributes"""
        attrNames=[]
        for kk in range(0,theAttrs.length):
            attrNames.append(theAttrs.item(kk).name)
        
        for kk in validAttrs:
            if (not kk.isOptional) and (kk.name not in attrNames):
                self.isValid = False
                self.addErrorString('Missing required attribute: %s at %d' % 
                                    (kk.name,lno))
        possibleAttrs=[]
        for kk in range(0,len(validAttrs)):
            possibleAttrs.append(validAttrs[kk].name)
            possibleAttrs.append('label')
            possibleAttrs.append('name')
            possibleAttrs.append('type')
        for ll in attrNames:
            if ll not in possibleAttrs:
                self.isValid = False
                self.addErrorString('Unknown attribute: %s at %d' % (ll,lno))
            
        return self.isValid

    def validateChildren(self):
        """Validate nodes in Input.xml"""
        line_no=0
        theroot=self.MyDom.documentElement
        for kk in theroot.childNodes:
            if kk.nodeName not in self.ValidFieldNames:
                self.isValid=False
                self.addErrorString('Invalid node: %s at %d' % (kk.nodeName,line_no))
            if kk.nodeName == '#text':
                line_no = line_no + 1

            if 'Label' == kk.nodeName:
                if kk.getAttribute('type') not in self.ValidLabelTypes:
                    self.isValid = False
                    self.addErrorString('Invalid label type: %s at %d' % (kk.getAttribute('type'),line_no))

            if 'InputField' == kk.nodeName:
                theAnswer=self.validateInputField(kk,line_no)
                if self.isValid:
                    self.isValid = theAnswer
        return self.isValid

    def getInputNames(self):
        return self.inputNames

if __name__ == '__main__':
    
    Input = InputXmlFile(sys.argv[1])
    isValid = Input.validateChildren()

    if True == isValid:
        print('%s is valid' % (Input.getInputFileName()))
    else:
        print('%s is not valid' % (Input.getInputFileName()))
        for kk in Input.getErrors():
            print('Error: %s' % (kk))
    print("Input Names:")
    for kk in Input.getInputNames():
        print("Name: " + kk)
