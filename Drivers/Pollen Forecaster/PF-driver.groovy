/**
 *  Pollen Forecaster
 *
 *  Design Usage:
 *  Retrieve data from Pollen.com. For use with Hubitat dashboards.
 *
 *  Copyright 2019 Bryan Turcotte (@bptworld)
 *  
 *  This App is free.  If you like and use this app, please be sure to give a shout out on the Hubitat forums to let
 *  people know that it exists!  Thanks.
 *
 *  Remember...I am not a programmer, everything I do takes a lot of time and research (then MORE research)!
 *  Donations are never necessary but always appreciated.  Donations to support development efforts are accepted via: 
 *
 *  Paypal at: https://paypal.me/bptworld
 * 
 *  Unless noted in the code, ALL code contained within this app is mine. You are free to change, ripout, copy, modify or
 *  otherwise use the code in anyway you want. This is a hobby, I'm more than happy to share what I have learned and help
 *  the community grow. Have FUN with it!
 * 
 * ------------------------------------------------------------------------------------------------------------------------------
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
 * v2.0.6 - 01/03/2020 - Adjustment for AW2
 * v2.0.5 - 08/29/2019 - App Watchdog compatible
 * v2.0.4 - 05/12/2019 - Added Yesterday data by request
 * V1.0.3 - 04/16/2019 - Code cleanup, added importUrl
 * v2.0.0 - 04/10/2019 - Code cleanup. Added 'Tomorrow forecast', Updated display for Hubitat dashboard tile (@bptworld)
 * v1.0.0 - 12/10/2017 - Original ST 'Pollen Virtual Sensor' - Author: jschlackman (james@schlackman.org)
 *			ST version: https://github.com/jschlackman/PollenThing
 *
 */

import groovy.json.*
import groovy.transform.Field

def setVersion(){
    appName = "PollenForecaster"
	version = "v2.0.6" 
    dwInfo = "${appName}:${version}"
    sendEvent(name: "dwDriverInfo", value: dwInfo, displayed: true)
}

@SuppressWarnings('unused')
def updateVersion() {
    log.info "In updateVersion"
    setVersion()
}

metadata {
	definition (name: "Pollen Forecaster", namespace: "BPTWorld", author: "Bryan Turcotte", importUrl: "https://raw.githubusercontent.com/bptworld/Hubitat/master/Drivers/Pollen%20Forecaster/PF-driver.groovy") {
        capability "Actuator"
		capability "Sensor"
		capability "Polling"

		attribute "indexYesterday", "number"
		attribute "categoryYesterday", "string"
		attribute "triggersYesterday", "string"
		attribute "indexToday", "number"
		attribute "categoryToday", "string"
		attribute "triggersToday", "string"
		attribute "indexTomorrow", "number"
		attribute "categoryTomorrow", "string"
		attribute "triggersTomorrow", "string"
		attribute "location", "string"
		
		attribute "yesterdayTile", "string"
		attribute "todayTile", "string"
		attribute "tomorrowTile", "string"
        
        attribute "dwDriverInfo", "string"
        command "updateVersion"
	}

	preferences {
		input name: "about", type: "paragraph", title: "Pollen Forecaster", description: "Retrieve data from Pollen.com. For use with Hubitat dashboards."
		input name: "zipCode", type: "text", title: "Zip Code", required: true, defaultValue: "${location.zipCode}"
		input "fontSizeIndex", "text", title: "Font Size-Index", required: true, defaultValue: "70"
		input "fontSizeTriggers", "text", title: "Font Size-Triggers", required: true, defaultValue: "60"
		input "logEnable", "bool", title: "Enable logging", required: true, defaultValue: false
	}
}

@SuppressWarnings('unused')
def installed() {
	poll()
}

@SuppressWarnings('unused')
def updated() {
	unschedule()
	updateVersion()
	runEvery3Hours(poll)
	if((Boolean)settings.logEnable) runIn(3600, "logsOff")
	poll()
}

@SuppressWarnings('unused')
void logsOff() {
	device.updateSetting("logEnable",[value:'false',type:"bool"])	
	log.debug "Diabling debug logs"
}

@SuppressWarnings('unused')
def uninstalled() {
	unschedule()
}

void poll() {
	if((Boolean)settings.logEnable) log.debug "In poll..."
	String pollenZip

	// Use hub zipcode if user has not defined their own
	if(zipCode) {
		pollenZip = zipCode
	} else {
		pollenZip = location.zipCode
	}
	
	if((Boolean)settings.logEnable) log.debug "Getting pollen data for ZIP: ${pollenZip}"

	def params = [
		uri: 'https://www.pollen.com/api/forecast/current/pollen/',
		path: pollenZip,
		headers: [Referer:'https://www.pollen.com'],
		timeout: 20
	]

	try {
		asynchttpGet('ahandler', params, [:])
	}
	catch (e) {
		if((Boolean)settings.logEnable) log.debug "Could not retrieve pollen data: $e"
		sendEvent(name: "location", value: "Could not retrieve data from API", displayed: true)
	}
}

@SuppressWarnings('unused')
void ahandler(resp, Map edata) {
	Integer responseCode = resp.status
	if(responseCode == 200 && resp.data) {
		Map rData = (Map)new JsonSlurper().parseText((String)resp.data)
		rData.Location.periods.each { period ->
			if(period.Type == 'Yesterday') {
				String catName
				Float indexNum = period.Index.toFloat()
				
				// Set the category according to index thresholds
				if (indexNum < 2.5) {catName = "Low"}
				else if (indexNum < 4.9) {catName = "Low-Medium"}
				else if (indexNum < 7.3) {catName = "Medium"}
				else if (indexNum < 9.7) {catName = "Medium-High"}
				else if (indexNum < 12) {catName = "High"}
				else {catName = "Unknown"}
				
				// Build the list of allergen triggers
				def triggersList = period.Triggers.inject([]) { result, entry ->
					result << "${entry.Name}"
				}.join(", ")
				state.indexYesterday = period.Index
				state.categoryYesterday = catName
				state.triggersYesterday = triggersList
				
				sendEvent(name: "indexYesterday", value: state.indexYesterday, displayed: true)
				sendEvent(name: "categoryYesterday", value: state.categoryYesterday, displayed: true)
				sendEvent(name: "triggersYesterday", value: state.triggersYesterday, displayed: true)
			}
			
			if(period.Type == 'Today') {
				String catName
				Float indexNum = period.Index.toFloat()
				
				// Set the category according to index thresholds
				if (indexNum < 2.5) {catName = "Low"}
				else if (indexNum < 4.9) {catName = "Low-Medium"}
				else if (indexNum < 7.3) {catName = "Medium"}
				else if (indexNum < 9.7) {catName = "Medium-High"}
				else if (indexNum < 12) {catName = "High"}
				else {catName = "Unknown"}
				
				// Build the list of allergen triggers
				def triggersList = period.Triggers.inject([]) { result, entry ->
					result << "${entry.Name}"
				}.join(", ")
				state.indexToday = period.Index
				state.categoryToday = catName
				state.triggersToday = triggersList
				
				sendEvent(name: "indexToday", value: state.indexToday, displayed: true)
				sendEvent(name: "categoryToday", value: state.categoryToday, displayed: true)
				sendEvent(name: "triggersToday", value: state.triggersToday, displayed: true)
			}
			
			if(period.Type == 'Tomorrow') {
				String catName
				Float indexNum = period.Index.toFloat()
				
				// Set the category according to index thresholds
				if (indexNum < 2.5) {catName = "Low"}
				else if (indexNum < 4.9) {catName = "Low-Medium"}
				else if (indexNum < 7.3) {catName = "Medium"}
				else if (indexNum < 9.7) {catName = "Medium-High"}
				else if (indexNum < 12) {catName = "High"}
				else {catName = "Unknown"}
				
				// Build the list of allergen triggers
				def triggersList = period.Triggers.inject([]) { result, entry ->
					result << "${entry.Name}"
				}.join(", ")
				state.indexTomorrow = period.Index
				state.categoryTomorrow = catName
				state.triggersTomorrow = triggersList
				
				sendEvent(name: "indexTomorrow", value: state.indexTomorrow, displayed: true)
				sendEvent(name: "categoryTomorrow", value: state.categoryTomorrow, displayed: true)
				sendEvent(name: "triggersTomorrow", value: state.triggersTomorrow, displayed: true)
			}
			state.location = rData.Location.DisplayLocation
			sendEvent(name: "location", value: state.location, displayed: true)
		}
	} else {
		if((Boolean)settings.logEnable) log.debug "Bad Response Could not retrieve pollen data: $e"
		sendEvent(name: "location", value: "Could not retrieve data from API", displayed: true)
	}
	yesterdayTileMap()
	todayTileMap()
	tomorrowTileMap()
}
/*	}
	catch (SocketTimeoutException e) {
		if(logEnable) log.debug "Connection to Pollen.com API timed out."
		sendEvent(name: "location", value: "Connection timed out while retrieving data from API", displayed: true)
	}
	catch (e) {
		if(logEnable) log.debug "Could not retrieve pollen data: $e"
		sendEvent(name: "location", value: "Could not retrieve data from API", displayed: true)
	}
	yesterdayTileMap()
	todayTileMap()
	tomorrowTileMap()
}*/

@SuppressWarnings('unused')
def configure() {
	poll()
}

void yesterdayTileMap() {
	if((Boolean)settings.logEnable) log.debug "In yesterdayTileMap..."
	state.appDataYesterday = "<table width='100%'>"
	state.appDataYesterday+= "<tr><td><div style='font-size:.${fontSizeTriggers}em;'>Pollen Forecast Today<br>${state.location}</div></td></tr>"
	state.appDataYesterday+= "<tr><td><div style='font-size:.${fontSizeIndex}em;'>${state.indexYesterday} - ${state.categoryYesterday}</div></td></tr>"
	state.appDataYesterday+= "<tr><td><div style='font-size:.${fontSizeTriggers}em;'>${state.triggersYesterday}</div></td></tr>"
	state.appDataYesterday+= "</table>"
	sendEvent(name: "yesterdayTile", value: state.appDataYesterday, displayed: true)
}

void todayTileMap() {
	if((Boolean)settings.logEnable) log.debug "In todayTileMap..."
	state.appDataToday = "<table width='100%'>"
	state.appDataToday+= "<tr><td><div style='font-size:.${fontSizeTriggers}em;'>Pollen Forecast Today<br>${state.location}</div></td></tr>"
	state.appDataToday+= "<tr><td><div style='font-size:.${fontSizeIndex}em;'>${state.indexToday} - ${state.categoryToday}</div></td></tr>"
	state.appDataToday+= "<tr><td><div style='font-size:.${fontSizeTriggers}em;'>${state.triggersToday}</div></td></tr>"
	state.appDataToday+= "</table>"
	sendEvent(name: "todayTile", value: state.appDataToday, displayed: true)
}

void tomorrowTileMap() {
	if((Boolean)settings.logEnable) log.debug "In tomorrowTileMap..."
	state.appDataTomorrow = "<table width='100%'>"
	state.appDataTomorrow+= "<tr><td><div style='font-size:.${fontSizeTriggers}em;'>Pollen Forecast Tomorrow<br>${state.location}</div></td></tr>"
	state.appDataTomorrow+= "<tr><td><div style='font-size:.${fontSizeIndex}em;'>${state.indexTomorrow} - ${state.categoryTomorrow}</div></td></tr>"
	state.appDataTomorrow+= "<tr><td><div style='font-size:.${fontSizeTriggers}em;'>${state.triggersTomorrow}</div></td></tr>"
	state.appDataTomorrow+= "</table>"
	sendEvent(name: "tomorrowTile", value: state.appDataTomorrow, displayed: true)
}
