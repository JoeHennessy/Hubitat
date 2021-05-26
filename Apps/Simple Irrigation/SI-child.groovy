/**
 *  ****************  Simple Irrigation Child App  ****************
 *
 *  Design Usage:
 *  For use with any valve device connected to your hose, like the Orbit Hose Water Timer. Features multiple timers and
 *  restrictions.
 *
 *  Copyright 2019-2021 Bryan Turcotte (@bptworld)
 * 
 *  This App is free.  If you like and use this app, please be sure to mention it on the Hubitat forums!  Thanks.
 *
 *  Remember...I am not a professional programmer, everything I do takes a lot of time and research!
 *  Donations are never necessary but always appreciated.  Donations to support development efforts are accepted via: 
 *
 *  Paypal at: https://paypal.me/bptworld
 * 
 *  Unless noted in the code, ALL code contained within this app is mine. You are free to change, ripout, copy, modify or
 *  otherwise use the code in anyway you want. This is a hobby, I'm more than happy to share what I have learned and help
 *  the community grow. Have FUN with it!
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
 *  App and Driver updates can be found at https://github.com/bptworld/Hubitat/
 *
 * ------------------------------------------------------------------------------------------------------------------------------
 *
 *  Changes:
 *
 *  2.0.7 - 05/26/21 - Added switch option
 *  2.0.6 - 06/16/20 - Added more push options
 *  2.0.5 - 06/02/20 - Fixed issues with scheduling
 *  2.0.4 - 06/02/20 - Lots of little adjustments
 *  2.0.3 - 05/31/20 - Some great changes/additions by @bdwilson. Thanks!
 *  2.0.2 - 04/29/20 - Check for days match before turning valve off
 *  2.0.1 - 04/27/20 - Cosmetic changes
 *  2.0.0 - 08/18/19 - Now App Watchdog compliant
 *  ---
 *  1.0.0 - 04/22/19 - Initial release.
 *
 */

import groovy.time.TimeCategory
import java.text.SimpleDateFormat

def setVersion(){
    state.name = "Simple Irrigation"
	state.version = "2.0.7"
}

definition(
    name: "Simple Irrigation Child",
    namespace: "BPTWorld",
    author: "Bryan Turcotte",
    description: "For use with any valve device connected to your hose, like the Orbit Hose Water Timer. Features multiple timers and restrictions.",
    category: "Convenience",
	parent: "BPTWorld:Simple Irrigation",
    iconUrl: "",
    iconX2Url: "",
    iconX3Url: "",
	importUrl: "https://raw.githubusercontent.com/bptworld/Hubitat/master/Apps/Simple%20Irrigation/SI-child.groovy",
)

preferences {
    page(name: "pageConfig")
}

def pageConfig() {
    dynamicPage(name: "", title: "", install: true, uninstall: true, refreshInterval:0) {
		display() 
        section("Instructions:", hideable: true, hidden: true) {
			paragraph "<b>Notes:</b>"
    		paragraph "For use with any valve device connected to your hose, like the Orbit Hose Water Timer. Features multiple timers and restrictions."
		}
		section(getFormat("header-green", "${getImage("Blank")}"+" Valve Devices")) {
            input "valveORswitch", "bool", title: "Use with a Value (off) or Switch (on)", defaultValue:false, required:true, submitOnChange:true
            if(valveORswitch) {
                input "switchDevice", "capability.switch", title: "Select Switch Device", required: true
            } else {
                input "valveDevice", "capability.valve", title: "Select Valve Device", required: true	
            }
		}
		section(getFormat("header-green", "${getImage("Blank")}"+" Schedule")) {
			input(name: "days", type: "enum", title: "Only water on these days", description: "Days to water", required: true, multiple: true, options: ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"])
			paragraph "Select up to 3 watering sessions per day."
			input "startTime1", "time", title: "Time to turn on 1", required: true, width: 6, submitOnChange:true
        	input "onLength1", "number", title: "Leave valve on for how long (in minutes)", required: true, width: 6
			paragraph "<hr>"
			input "startTime2", "time", title: "Time to turn on 2", required: false, width: 6, submitOnChange:true
        	input "onLength2", "number", title: "Leave valve on for how long (in minutes)", required: false, width: 6
			paragraph "<hr>"
			input "startTime3", "time", title: "Time to turn on 3", required: false, width: 6, submitOnChange:true
        	input "onLength3", "number", title: "Leave valve on for how long (in minutes)", required: false, width: 6
		}
		section(getFormat("header-green", "${getImage("Blank")}"+" Safety Features")) {
			paragraph "App can send the open/closed command several times with a 20 second delay between commands, until either max tries is reached or device reports that it is open/closed. Once max tries is reached, a notification will can be sent (if selected below)."
			input "maxTriesOn", "number", title: "Attempts to OPEN", required: true, defaultValue: 3, width: 6
			input "maxTriesOff", "number", title: "Attempts to CLOSE", required: true, defaultValue: 5, width: 6
		}
		section(getFormat("header-green", "${getImage("Blank")}"+" Check the Weather")) {
			paragraph "Disable app using any 'Switch' type device. I highly recommend using WATO to turn a virtual switch on/off based on any device parameter."
			paragraph "If ANY of the options below are ON, watering will be cancelled."
			input "rainSensor", "capability.switch", title: "Rain Switch", required: false
			input "windSensor", "capability.switch", title: "Wind Switch", required: false
			input "otherSensor", "capability.switch", title: "Other Switch", required: false
		}
		section(getFormat("header-green", "${getImage("Blank")}"+" Notification Options")) {
			input "sendPushMessage", "capability.notification", title: "Send a notification?", multiple:true, required:false, submitOnChange:true
            if(sendPushMessage) {
                input "sendInfoPushMessage", "bool", title: "Send informational message regarding value status.", defaultValue:false, required:true
                input "sendTroublePushMessage", "bool", title: "Send message when value may be in trouble.", defaultValue:false, required:true
                input "sendSafetyPushMessage", "bool", title: "Send close notification even if weather switch has cancelled the schedule.", defaultValue:false, required:true
            }
		}
		section(getFormat("header-green", "${getImage("Blank")}"+" General")) {
            label title: "Enter a name for this automation", required: false
            input "logEnable", "bool", defaultValue:false, title: "Enable Debug Logging", description: "Enable extra logging for debugging."
		}
		display2()
	}
}

def installed() {
    log.debug "Installed with settings: ${settings}"
	initialize()
}

def updated() {	
    if(logEnable) log.debug "Updated with settings: ${settings}"
	unschedule()
	initialize()
}

def initialize() {
    setDefaults()
	if(startTime1) schedule(startTime1, settingUpHandler, [overwrite: false])
	if(startTime2) schedule(startTime2, settingUpHandler, [overwrite: false])
	if(startTime3) schedule(startTime3, settingUpHandler, [overwrite: false])
}
	
def settingUpHandler() {
    resetTrys()
    timeHandler()
    turnValveOn()
}

def turnValveOn() {
    if(state.valveTry == null) state.valveTry = 0
    if(state.valveTry == 0) { if(logEnable) log.warn "*************** Start Valve On - Simple Irrigation Child - (${state.version}) ***************" }	
    if(valveORswitch) {
        if(switchDevice) state.switchStatus = valveDevice.switchDevice("switch")
    } else {
        if(valveDevice) state.valveStatus = valveDevice.currentValue("valve")
    }
	dayOfTheWeekHandler()
	checkForWeather()
	if(state.daysMatch) {
		if(state.canWater) {
            if(logEnable) log.debug "In turnValveOn (${state.version})"
			if(state.valveStatus == "closed") {
				state.valveTry = state.valveTry + 1
                if(logEnable) log.debug "In turnValveOn - trying to turn on - Attempt ${state.valveTry} - will check again in 20 seconds"
                if(valveORswitch) {
				    switchDevice.on()
                } else {
                    valveDevice.open()
                }
				if(state.valveTry <= maxTriesOn) runIn(20, turnValveOn)		// Repeat for safety
				if(state.valveTry > maxTriesOn) {
                    if(valveORswitch) {
                        log.warn "${switchDevice} didn't turn on after ${maxTriesOn} tries."
                        state.msg = "${switchDevice} didn't turn on after ${maxTriesOn} tries. Please CHECK device."
                    } else {
                        log.warn "${valveDevice} didn't open after ${maxTriesOn} tries."
                        state.msg = "${valveDevice} didn't open after ${maxTriesOn} tries. Please CHECK device."
                    }
					if(sendTroublePushMessage) pushHandler()
                    resetTrys()
				}
			} else {
                if(logEnable) log.debug "In turnValveOn - theSchedule: ${state.theSchedule}"
                if(state.theSchedule == "1") { 
                    if(onLength1 == null) onLength1 = 10
                    onLength = onLength1
                } else if(state.theSchedule == "2") {
                        if(onLength2 == null) onLength2 = 10
                        onLength = onLength2
                } else if(state.theSchedule == "3") {
                        if(onLength3 == null) onLength3 = 10
                        onLength = onLength3
                } else {
                    onLength = 10
                }
                //log.warn "onLength: ${onLength}"
                def delay = onLength * 60
                if(valveORswitch) {
                    if(logEnable) log.debug "In turnValveOn - Valve is now ${state.deviceStatus}, Setting valve timer to off in ${onLength} minutes"
                    log.warn "${deviceStatus} is now ${state.deviceStatus}"
                    state.msg = "${deviceStatus} is now ${state.deviceStatus}"
                } else {
                    if(logEnable) log.debug "In turnValveOn - Valve is now ${state.valveStatus}, Setting valve timer to off in ${onLength} minutes"
                    log.warn "${valveDevice} is now ${state.valveStatus}"
                    state.msg = "${valveDevice} is now ${state.valveStatus}"
                }
				if(sendInfoPushMessage) pushHandler()
                resetTrys()
				runIn(delay, turnValveOff)
			}
		} else {
            if(valveORswitch) {
                log.info "${app.label} didn't pass weather check. ${swtichDevice} not turned on."
            } else {
                log.info "${app.label} didn't pass weather check. ${valveDevice} not turned on."
            }
            resetTrys()
			turnValveOff()
		}
	} else {
        if(valveORswitch) {
            log.info "${app.label} didn't pass day check. Water not turned on."
            state.msg = "${app.label} didn't pass day check. ${switchDevice} will not turn on."
        } else {
            log.info "${app.label} didn't pass day check. Water not turned on."
            state.msg = "${app.label} didn't pass day check. ${valveDevice} will not turn on."
        }
        resetTrys()
		turnValveOff()
	}
    if(state.valveTry == 0) { if(logEnable) log.warn "*************** End Valve On - Simple Irrigation Child - (${state.version}) ***************" }
}

def turnValveOff() {
    if(state.valveTryOff == null) state.valveTryOff = 0
    if(state.valveTryOff == 0) { if(logEnable) log.warn "*************** Start Valve Off - Simple Irrigation Child - (${state.version}) ***************" }
    dayOfTheWeekHandler()
	if(state.daysMatch) {
        if(logEnable) log.debug "In turnValveOff (${state.version})"
        if(valveORswitch) {
        if(switchDevice) state.switchStatus = valveDevice.switchDevice("switch")
    } else {
        if(valveDevice) state.valveStatus = valveDevice.currentValue("valve")
    }
        if(state.valveStatus == "open") {
            state.valveTryOff = state.valveTryOff + 1
            if(logEnable) log.debug "In turnValveOff - trying to turn off - Attempt ${state.valveTryOff} - will check again in 20 seconds"
            if(valveORswitch) {
				    switchDevice.off()
                } else {
                    valveDevice.close()
                }
            if(state.valveTryOff <= maxTriesOff) runIn(20, turnValveOff)		// Repeat for safety
            if(state.valveTryOff > maxTriesOff) {
                if(valveORswitch) {
                    log.warn "${switchDevice} didn't turn off after ${maxTriesOff} tries."
                    state.msg = "${switchDevice} didn't turn off after ${maxTriesOff} tries. Please CHECK device."
                } else {
                    log.warn "${valveDevice} didn't close after ${maxTriesOff} tries."
                    state.msg = "${valveDevice} didn't close after ${maxTriesOff} tries. Please CHECK device."
                }
                if(sendTroublePushMessage) pushHandler()
                resetTrys()
            }
        } else {
            if(logEnable) log.debug "In turnValveOff - Valve is now ${state.valveStatus}"
            if(valveORswitch) {
                log.warn "${switchDevice} is now ${state.deviceStatus}"
                state.msg = "${switchDevice} is now ${state.deviceStatus}"
            } else {
                log.warn "${valveDevice} is now ${state.valveStatus}"
                state.msg = "${valveDevice} is now ${state.valveStatus}"
            }
            if (!state.canWater) {
				state.msg = "${valveDevice} is now ${state.valveStatus}. Watering session skipped due to weather switch."
                resetTrys()
			}
			if ((!state.canWater && sendSafetyPushMessage == true) || (state.canWater)) {
            	if(sendInfoPushMessage) pushHandler()
                resetTrys()
			}
        }
    }
    if(state.valveTryOff == 0) { if(logEnable) log.warn "*************** End Valve Off - Simple Irrigation Child - (${state.version}) ***************" }
}

def resetTrys() {
    if(logEnable) log.debug "In resetTrys (${state.version})"
    state.valveTry = 0
    state.valveTryOff = 0
}

def checkForWeather() {
	if(logEnable) log.debug "In checkForWeather (${state.version})"
	if(rainSensor) state.rainDevice = rainSensor.currentValue("switch")
	if(windSensor) state.windDevice = windSensor.currentValue("switch")
	if(otherSensor) state.otherDevice = otherSensor.currentValue("switch")
	if(state.rainDevice == "on" || state.windDevice == "on" || state.otherDevice == "on") {
		if(logEnable) log.debug "In checkForWeather - Weather Check failed."
		state.canWater = false
	} else {
		if(logEnable) log.debug "In checkForWeather - Weather Check passed."
		state.canWater = true
	}
}

def dayOfTheWeekHandler() {
	if(logEnable) log.debug "In dayOfTheWeek (${state.version})"
    
    def df = new java.text.SimpleDateFormat("EEEE")
    df.setTimeZone(location.timeZone)
    def day = df.format(new Date())
    def dayCheck = days.contains(day)

    if(dayCheck) {
		if(logEnable) log.debug "In dayOfTheWeekHandler - Days of the Week Passed"
		state.daysMatch = true
	} else {
		if(logEnable) log.debug "In dayOfTheWeekHandler - Days of the Week Check Failed"
		state.daysMatch = false
	}
}

def timeHandler() {
	if(logEnable) log.debug "In timeHandler (${state.version})"

    def date = new Date()
    tTime = date.format("yyyy-MM-dd'T'HH:mm:ss.'000'Z")
    
    sTime1 = startTime1
    sTime2 = startTime2
    sTime3 = startTime3
    
    if(logEnable) log.debug "In timeHandler - startTime1: ${sTime1} - theTime: ${tTime}"
    if(sTime1 == tTime) {
		if(logEnable) log.debug "In timeHandler - Time Check Matches Timer 1"
		state.theSchedule = "1"
    }
    if(sTime2 == tTime) {
		if(logEnable) log.debug "In timeHandler - Time Check Matches Timer 2"
		state.theSchedule = "2"
    }
    if(sTime3 == tTime) {
		if(logEnable) log.debug "In timeHandler - Time Check Matches Timer 3"
		state.theSchedule = "3"
    }
}

def pushHandler(){
	if(logEnable) log.debug "In pushNow (${state.version})"
	theMessage = "${app.label} - ${state.msg}"
	if(logEnable) log.debug "In pushNow...Sending message: ${theMessage}"
   	sendPushMessage.deviceNotification(theMessage)
    state.msg = ""
}

// ********** Normal Stuff **********

def setDefaults(){
	if(logEnable == null){logEnable = false}
	if(state.rainDevice == null){state.rainDevice = "off"}
	if(state.windDevice == null){state.windDevice = "off"}
	if(state.otherDevice == null){state.otherDevice = "off"}
	if(state.daysMatch == null){state.daysMatch = false}
	if(state.msg == null){state.msg = ""}
    state.valveTry = 0
    state.valveTryOff = 0
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
    timeSinceNewHeaders()   
    if(state.totalHours > 4) {
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
        }
        catch (e) { }
    }
    if(state.headerMessage == null) state.headerMessage = "<div style='color:#1A77C9'><a href='https://github.com/bptworld/Hubitat' target='_blank'>BPTWorld Apps and Drivers</a></div>"
    if(state.footerMessage == null) state.footerMessage = "<div style='color:#1A77C9;text-align:center'>BPTWorld Apps and Drivers<br><a href='https://github.com/bptworld/Hubitat' target='_blank'>Donations are never necessary but always appreciated!</a><br><a href='https://paypal.me/bptworld' target='_blank'><b>Paypal</b></a></div>"
}

def timeSinceNewHeaders() { 
    if(state.previous == null) { 
        prev = new Date()
    } else {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        prev = dateFormat.parse("${state.previous}".replace("+00:00","+0000"))
    }
    def now = new Date()
    use(TimeCategory) {       
        state.dur = now - prev
        state.days = state.dur.days
        state.hours = state.dur.hours
        state.totalHours = (state.days * 24) + state.hours
    }
    state.previous = now
}
