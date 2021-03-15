/**
 *  ****************  Send IP2IR Parent App  ****************
 *
 *  Design Usage:
 *  This app is designed to send commands to an iTach IP2IR device.
 *
 *  IR Codes can be found using Global Cache Control Tower IR Database, https://irdb.globalcache.com/
 *
 *  Copyright 2018-2021 Bryan Turcotte (@bptworld)
 *
 *  Thanks to Carson Dallum's (@cdallum) for the original IP2IR driver code that I based mine off of.
 *  
 *  This App is free.  If you like and use this app, please be sure to mention it on the Hubitat forums!  Thanks.
 *
 *  Remember...I am not a programmer, everything I do takes a lot of time and research!
 *  Donations are never necessary but always appreciated.  Donations to support development efforts are accepted via: 
 *
 *  Paypal at: https://paypal.me/bptworld
 *
 *-------------------------------------------------------------------------------------------------------------------
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 * ------------------------------------------------------------------------------------------------------------------------------
 *
 *  If modifying this project, please keep the above header intact and add your comments/credits below - Thank you! -  @BPTWorld
 *
 *  App and Driver updates can be found at https://github.com/bptworld/Hubitat
 *
 * ------------------------------------------------------------------------------------------------------------------------------
 *
 *  Changes:
 *
 *  2.0.6 - 03/15/21 - Adjustment
 *  2.0.5 - 01/24/21 - Lots of changes for easier first time setup
 *  2.0.4 - 01/16/21 - Cosmetic changes
 *  2.0.3 - 06/11/20 - Added 'Digit Separator' to Advanced Options
 *  2.0.2 - 04/27/20 - Cosmetic changes
 *  2.0.1 - 10/20/19 - Moved telnetDevice to parent app
 *  2.0.0 - 08/18/19 - Now App Watchdog compliant
 *  ---
 *  1.0.0 - 10/15/18 - Initial release
 */

def setVersion(){
    state.name = "Send IP2IR"
	state.version = "2.0.6"
}

definition(
    name:"Send IP2IR",
    namespace: "BPTWorld",
    author: "Bryan Turcotte",
    description: "This app is designed to send commands to an iTach IP2IR device.",
    category: "Convenience",
    iconUrl: "",
    iconX2Url: "",
    iconX3Url: ""
)

preferences {
     page name: "mainPage", title: "", install: true, uninstall: true
} 

def installed() {
    log.debug "Installed with settings: ${settings}"
    initialize()
}

def updated() {
    log.debug "Updated with settings: ${settings}"
    unsubscribe()
    initialize()
}

def initialize() {
    log.info "There are ${childApps.size()} child apps"
    childApps.each {child ->
    log.info "Child app: ${child.label}"
    }
}

def mainPage() {
    dynamicPage(name: "mainPage") {
    	installCheck()
		if(state.appInstalled == 'COMPLETE'){
			section("${getImage('instructions')} <b>Instructions:</b>", hideable: true, hidden: true) {
				paragraph "There are 4 types of Triggers that can be made."
        		paragraph "<b>Switch:</b><br>To turn anything on/off. ie. Television, Stereo, Cable Box, etc. Remember, it's okay to put the same code in box on and off if necessary."
    			paragraph "<b>Button:</b><br>Used to send one command. ie. Volume Up, Channel Down, etc. Note: this can not be used with Google Assistant."
        		paragraph "<b>Channel_Switch:</b><br>Used to send 1 to 4 commands at the same time. This is used to send Channels numbers based on the Presets in the Parent app."
        		paragraph "<b>Channel_Button:</b><br>Also, used to send 1 to 4 commands at the same time. This is used to send Channels numbers based on the Presets in the Parent app. Note: this can not be used with Google Assistant."
        		paragraph "<b>Important:</b><br>Each child app takes a device to trigger the commands, so be sure to create either a Virtual Switch or Virtual Button before trying to create a child app."
				paragraph "<b>Google Assistant Notes:</b><br>Google Assistant only works with switches. If creating virtual switches for channels, be sure to use the 'Enable auto off' @ '500ms' to give the effect of a button in a Dashboard but still be able to tell Google to control it."
				}
            section(getFormat("header-green", "${getImage("Blank")}"+" Telnet Setup")) {
                paragraph "Send IP2IR needs a Telnet device to operate. This is the device you'll use to control other things."
                input "useExistingDevice", "bool", title: "Use existing device (off) or have Send IP2IR create a new one for you (on)", defaultValue:false, submitOnChange:true
                if(useExistingDevice) {
                    input "dataName", "text", title: "Enter a name for this vitual Device (ie. 'Send IP2IR - Telnet')", required:true, submitOnChange:true
                    paragraph "<b>A device will automaticaly be created for you as soon as you click outside of this field.</b>"
                    if(dataName) createDataChildDevice()
                    if(statusMessageD == null) statusMessageD = "Waiting on status message..."
                    paragraph "${statusMessageD}"
                }
                input "telnetDevice", "capability.telnet", title: "Virtual Device created for Send IP2IR", required:true, multiple:false
                if(!useExistingDevice) {
                    app.removeSetting("dataName")
                    paragraph "<small>* Device must use the 'IP2IR Telnet' Driver.</small>"
                }
                if(telnetDevice) {
                    paragraph "<b>Be sure to go into the the Telnet Device and finish setting it up BEFORE continuing with this app.</b>"
                }
    	    }
            
  			section(getFormat("header-green", "${getImage("Blank")}"+" Child Apps")) {
                paragraph "<b>Please enter in the Preset Values in 'Advanced Config' BEFORE creating Child Apps</b>"
				app(name: "anyOpenApp", appName: "Send IP2IR Child", namespace: "BPTWorld", title: "<b>Add a new 'Send IP2IR' child</b>", multiple: true)
			}
            
			section(getFormat("header-green", "${getImage("Blank")}"+" Advanced Config")) {
            	paragraph "<b>Please enter in the Preset Values in 'Advanced Config' BEFORE creating Child Apps</b>"
			}
            section("Advanced Config:", hideable: true, hidden: true) {
            	input "msgDigit1", "text", required: true, title: "IR Code to Send - 1", defaultValue: ""
                input "msgDigit2", "text", required: true, title: "IR Code to Send - 2", defaultValue: ""
                input "msgDigit3", "text", required: true, title: "IR Code to Send - 3", defaultValue: ""
                input "msgDigit4", "text", required: true, title: "IR Code to Send - 4", defaultValue: ""
                input "msgDigit5", "text", required: true, title: "IR Code to Send - 5", defaultValue: ""
                input "msgDigit6", "text", required: true, title: "IR Code to Send - 6", defaultValue: ""
                input "msgDigit7", "text", required: true, title: "IR Code to Send - 7", defaultValue: ""
                input "msgDigit8", "text", required: true, title: "IR Code to Send - 8", defaultValue: ""
                input "msgDigit9", "text", required: true, title: "IR Code to Send - 9", defaultValue: ""
                input "msgDigit0", "text", required: true, title: "IR Code to Send - 0", defaultValue: ""
                input "msgDigitE", "text", required: false, title: "IR Code to Send - Enter", defaultValue: ""
                input "msgDigitDS", "text", required: false, title: "IR Code to Send - Digit Separator (-)", defaultValue: ""
            }
            
            section(getFormat("header-green", "${getImage("Blank")}"+" General")) {
       			label title: "Enter a name for parent app (optional)", required:false
 			}
		}
		display2()
	}
}

def installCheck(){  
    display()
	state.appInstalled = app.getInstallationState() 
	if(state.appInstalled != 'COMPLETE'){
		section{paragraph "Please hit 'Done' to install '${app.label}' parent app "}
  	}
  	else{
    	log.info "Parent Installed OK"
  	}
}

def createDataChildDevice() {    
    if(logEnable) log.debug "In createDataChildDevice (${state.version})"
    statusMessageD = ""
    if(!getChildDevice(dataName)) {
        if(logEnable) log.debug "In createDataChildDevice - Child device not found - Creating device: ${dataName}"
        try {
            addChildDevice("BPTWorld", "IP2IR Telnet", dataName, 1234, ["name": "${dataName}", isComponent: false])
            if(logEnable) log.debug "In createDataChildDevice - Child device has been created! (${dataName})"
            statusMessageD = "<b>Device has been been created. (${dataName})</b>"
        } catch (e) { if(logEnable) log.debug "Send IP2IR unable to create device - ${e}" }
    } else {
        statusMessageD = "<b>Device Name (${dataName}) already exists.</b>"
    }
    return statusMessageD
}

def getImage(type) {					// Modified from @Stephack Code
    def loc = "<img src=https://raw.githubusercontent.com/bptworld/Hubitat/master/resources/images/"
    if(type == "Blank") return "${loc}blank.png height=40 width=5}>"
    if(type == "checkMarkGreen") return "${loc}checkMarkGreen2.png height=30 width=30>"
    if(type == "optionsGreen") return "${loc}options-green.png height=30 width=30>"
    if(type == "optionsRed") return "${loc}options-red.png height=30 width=30>"
    if(type == "instructions") return "${loc}instructions.png height=30 width=30>"
    if(type == "logo") return "${loc}logo.png height=60>"
}

def getFormat(type, myText="") {			// Modified from @Stephack Code   
	if(type == "header-green") return "<div style='color:#ffffff;font-weight: bold;background-color:#81BC00;border: 1px solid;box-shadow: 2px 3px #A9A9A9'>${myText}</div>"
    if(type == "line") return "<hr style='background-color:#1A77C9; height: 1px; border: 0;'>"
    if(type == "title") return "<h2 style='color:#1A77C9;font-weight: bold'>${myText}</h2>"
}

def display() {
    setVersion()
    getHeaderAndFooter()
    theName = app.label
    if(theName == null || theName == "") theName = "New Child App"
    section (getFormat("title", "${getImage("logo")}" + " ${state.name} - ${theName}")) {
        paragraph "${state.headerMessage}"
		paragraph getFormat("line")
	}
}

def display2() {
	section() {
		paragraph getFormat("line")
		paragraph "<div style='color:#1A77C9;text-align:center;font-size:20px;font-weight:bold'>${state.name} - ${state.version}</div>"
        paragraph "${state.footerMessage}"
	}       
}

def getHeaderAndFooter() {
    if(logEnable) log.debug "In getHeaderAndFooter (${state.version})"
    def params = [
	    uri: "https://raw.githubusercontent.com/bptworld/Hubitat/master/info.json",
		requestContentType: "application/json",
		contentType: "application/json",
		timeout: 30
	]
    
    try {
        def result = null
        httpGet(params) { resp ->
            state.headerMessage = resp.data.headerMessage
            state.footerMessage = resp.data.footerMessage
        }
        if(logEnable) log.debug "In getHeaderAndFooter - headerMessage: ${state.headerMessage}"
        if(logEnable) log.debug "In getHeaderAndFooter - footerMessage: ${state.footerMessage}"
    }
    catch (e) {
        state.headerMessage = "<div style='color:#1A77C9'><a href='https://github.com/bptworld/Hubitat' target='_blank'>BPTWorld Apps and Drivers</a></div>"
        state.footerMessage = "<div style='color:#1A77C9;text-align:center'>BPTWorld<br><a href='https://github.com/bptworld/Hubitat' target='_blank'>Find more apps on my Github, just click here!</a><br><a href='https://paypal.me/bptworld' target='_blank'>Paypal</a></div>"
    }
}
