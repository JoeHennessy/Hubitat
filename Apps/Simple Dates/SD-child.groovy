/**
 *  ****************  Simple Dates Child App  ****************
 *
 *  Design Usage:
 *  Create a simple countdown to your most important dates.
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
 *  2.0.4 - 07/30/21 - Added Long Range Dates, app now runs when saved.
 *  2.0.3 - 04/27/20 - Cosmetic changes
 *  2.0.2 - 11/30/19 - Removed 'is this a school day/work day' (they didn't do anything), added device to control
 *  2.0.1 - 11/16/19 - Date output now available in two formats - MM/DD/YYYY or DD/MM/YYY
 *  2.0.0 - 08/18/19 - Now App Watchdog compliant
 *  1.0.0 - 06/03/19 - Initial release.
 *
 */

import groovy.time.TimeCategory

def setVersion(){
    state.name = "Simple Dates"
	state.version = "2.0.4"
}

definition(
    name: "Simple Dates Child",
    namespace: "BPTWorld",
    author: "Bryan Turcotte",
    description: "Create a simple countdown to your most important dates.",
    category: "Convenience",
	parent: "BPTWorld:Simple Dates",
    iconUrl: "",
    iconX2Url: "",
    iconX3Url: "",
	importUrl: "https://raw.githubusercontent.com/bptworld/Hubitat/master/Apps/Simple%20Dates/SD-child.groovy",
)

preferences {
    page(name: "pageConfig")
}

def pageConfig() {
    dynamicPage(name: "", title: "", install: true, uninstall: true) {
		display() 
        section("${getImage('instructions')} <b>Instructions:</b>", hideable: true, hidden: true) {
			paragraph "<b>Notes:</b>"
            paragraph "Simple Dates - Create a simple countdown to your most important dates."
            paragraph "* Remember, all dates can control the same switch if you want"
		}
		section(getFormat("header-green", "${getImage("Blank")}"+" Event Dates")) {
            paragraph "Events will count down to the next occurrence of the date."
			input "month1", "enum", title: "Select Month", required: false, multiple: false, width: 4, submitOnChange: true, options: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"]
			if(month1 == "1" || month1 == "3" || month1 == "5" || month1 == "7" || month1 == "8" || month1 == "10" || month1 == "12") input "day1", "enum", title: "Select Day", required: false, multiple: true, width: 4, options: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"]
			if(month1 == "4" || month1 == "6" || month1 == "9" || month1 == "11") input "day1", "enum", title: "Select Day", required: false, multiple: true, width: 4, options: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30"]
			if(month1 == "2") input "day1", "enum", title: "Select Day", required: false, multiple: true, width: 4, options: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28"]
			if(month1) input "reminder1", "text", title: "Event", required: false, multiple: false, defaultValue: "", width: 4
            if(month1) paragraph "Use this option to override lighting, turn off alarms and more!<br><small>* Automaticaly turns 'on' on the selected day(s), else turns off</small>"
            if(month1) input "dControl1", "capability.switch", title: "Turn this Switch on/off based on the day", submitOnChange: true, multiple: false
		paragraph getFormat("line")
            
            if(month1) {
                input "month2", "enum", title: "Select Month", required: false, multiple: false, width: 4, submitOnChange: true, options: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"]
                if(month2 == "1" || month2 == "3" || month2 == "5" || month2 == "7" || month2 == "8" || month2 == "10" || month2 == "12") input "day2", "enum", title: "Select Day", required: false, multiple: true, width: 4, options: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"]
                if(month2 == "4" || month2 == "6" || month2 == "9" || month2 == "11") input "day2", "enum", title: "Select Day", required: false, multiple: true, width: 4, options: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30"]
                if(month2 == "2") input "day2", "enum", title: "Select Day", required: false, multiple: true, width: 4, options: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28"]
                if(month2) input "reminder2", "text", title: "Event", required: false, multiple: false, defaultValue: "", width: 4
                if(month2) paragraph "Use this option to override lighting, turn off alarms and more!<br><small>* Automaticaly turns 'on' on the selected day(s), else turns off</small>"
                if(month2) input "dControl2", "capability.switch", title: "Turn this Switch on/off based on the day", submitOnChange: true, multiple: false
                paragraph getFormat("line")
            }
            if(month2) {
                input "month3", "enum", title: "Select Month", required: false, multiple: false, width: 4, submitOnChange: true, options: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"]
                if(month3 == "1" || month3 == "3" || month3 == "5" || month3 == "7" || month3 == "8" || month3 == "10" || month3 == "12") input "day3", "enum", title: "Select Day", required: false, multiple: true, width: 4, options: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"]
                if(month3 == "4" || month3 == "6" || month3 == "9" || month3 == "11") input "day3", "enum", title: "Select Day", required: false, multiple: true, width: 4, options: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30"]
                if(month3 == "2") input "day3", "enum", title: "Select Day", required: false, multiple: true, width: 4, options: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28"]
                if(month3) input "reminder3", "text", title: "Event", required: false, multiple: false, defaultValue: "", width: 4
                if(month3) paragraph "Use this option to override lighting, turn off alarms and more!<br><small>* Automaticaly turns 'on' on the selected day(s), else turns off</small>"
                if(month3) input "dControl3", "capability.switch", title: "Turn this Switch on/off based on the day", submitOnChange: true, multiple: false
                paragraph getFormat("line")
            }
            if(month3) {
                input "month4", "enum", title: "Select Month", required: false, multiple: false, width: 4, submitOnChange: true, options: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"]
                if(month4 == "1" || month4 == "3" || month4 == "5" || month4 == "7" || month4 == "8" || month4 == "10" || month4 == "12") input "day4", "enum", title: "Select Day", required: false, multiple: true, width: 4, options: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"]
                if(month4 == "4" || month4 == "6" || month4 == "9" || month4 == "11") input "day4", "enum", title: "Select Day", required: false, multiple: true, width: 4, options: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30"]
                if(month4 == "2") input "day4", "enum", title: "Select Day", required: true, multiple: true, width: 4, options: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28"]
                if(month4) input "reminder4", "text", title: "Event", required: false, multiple: false, defaultValue: "", width: 4
                if(month4) paragraph "Use this option to override lighting, turn off alarms and more!<br><small>* Automaticaly turns 'on' on the selected day(s), else turns off</small>"
                if(month4) input "dControl4", "capability.switch", title: "Turn this Switch on/off based on the day", submitOnChange: true, multiple: false
                paragraph getFormat("line")
            }
            if(month4) {
                input "month5", "enum", title: "Select Month", required: false, multiple: false, width: 4, submitOnChange: true, options: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"]
                if(month5 == "1" || month5 == "3" || month5 == "5" || month5 == "7" || month5 == "8" || month5 == "10" || month5 == "12") input "day5", "enum", title: "Select Day", required: false, multiple: true, width: 4, options: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"]
                if(month5 == "4" || month5 == "6" || month5 == "9" || month5 == "11") input "day5", "enum", title: "Select Day", required: false, multiple: true, width: 4, options: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30"]
                if(month5 == "2") input "day5", "enum", title: "Select Day", required: true, multiple: true, width: 4, options: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28"]
                if(month5) input "reminder5", "text", title: "Event", required: false, multiple: false, defaultValue: "", width: 4
                if(month5) paragraph "Use this option to override lighting, turn off alarms and more!<br><small>* Automaticaly turns 'on' on the selected day(s), else turns off</small>"
                if(month5) input "dControl5", "capability.switch", title: "Turn this Switch on/off based on the day", submitOnChange: true, multiple: false
                paragraph getFormat("line")
            }
            if(month5) {
                input "month6", "enum", title: "Select Month", required: false, multiple: false, width: 4, submitOnChange: true, options: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"]
                if(month6 == "1" || month6 == "3" || month6 == "5" || month6 == "7" || month6 == "8" || month6 == "10" || month6 == "12") input "day6", "enum", title: "Select Day", required: false, multiple: true, width: 4, options: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"]
                if(month6 == "4" || month6 == "6" || month6 == "9" || month6 == "11") input "day6", "enum", title: "Select Day", required: false, multiple: true, width: 4, options: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30"]
                if(month6 == "2") input "day6", "enum", title: "Select Day", required: true, multiple: true, width: 4, options: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28"]
                if(month6) input "reminder6", "text", title: "Event", required: false, multiple: false, defaultValue: "", width: 4
                if(month6) paragraph "Use this option to override lighting, turn off alarms and more!<br><small>* Automaticaly turns 'on' on the selected day(s), else turns off</small>"
                if(month6) input "dControl6", "capability.switch", title: "Turn this Switch on/off based on the day", submitOnChange: true, multiple: false
                paragraph getFormat("line")
            }
            if(month6) {
                input "month7", "enum", title: "Select Month", required: false, multiple: false, width: 4, submitOnChange: true, options: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"]
                if(month7 == "1" || month7 == "3" || month7 == "5" || month7 == "7" || month7 == "8" || month7 == "10" || month7 == "12") input "day7", "enum", title: "Select Day", required: false, multiple: true, width: 4, options: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"]
                if(month7 == "4" || month7 == "6" || month7 == "9" || month7 == "11") input "day7", "enum", title: "Select Day", required: false, multiple: true, width: 4, options: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30"]
                if(month7 == "2") input "day7", "enum", title: "Select Day", required: true, multiple: true, width: 4, options: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28"]
                if(month7) input "reminder7", "text", title: "Event", required: false, multiple: false, defaultValue: "", width: 4
                if(month7) paragraph "Use this option to override lighting, turn off alarms and more!<br><small>* Automaticaly turns 'on' on the selected day(s), else turns off</small>"
                if(month7) input "dControl7", "capability.switch", title: "Turn this Switch on/off based on the day", submitOnChange: true, multiple: false
                paragraph getFormat("line")
            }
            if(month7) {
                input "month8", "enum", title: "Select Month", required: false, multiple: false, width: 4, submitOnChange: true, options: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"]
                if(month8 == "1" || month8 == "3" || month8 == "5" || month8 == "7" || month8 == "8" || month8 == "10" || month8 == "12") input "day8", "enum", title: "Select Day", required: false, multiple: true, width: 4, options: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"]
                if(month8 == "4" || month8 == "6" || month8 == "9" || month8 == "11") input "day8", "enum", title: "Select Day", required: false, multiple: true, width: 4, options: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30"]
                if(month8 == "2") input "day8", "enum", title: "Select Day", required: true, multiple: true, width: 4, options: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28"]
                if(month8) input "reminder8", "text", title: "Event", required: false, multiple: false, defaultValue: "", width: 4
                if(month8) paragraph "Use this option to override lighting, turn off alarms and more!<br><small>* Automaticaly turns 'on' on the selected day(s), else turns off</small>"
                if(month8) input "dControl8", "capability.switch", title: "Turn this Switch on/off based on the day", submitOnChange: true, multiple: false
                paragraph getFormat("line")
            }
            if(month8) {
                input "month9", "enum", title: "Select Month", required: false, multiple: false, width: 4, submitOnChange: true, options: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"]
                if(month9 == "1" || month9 == "3" || month9 == "5" || month9 == "7" || month9 == "8" || month9 == "10" || month9 == "12") input "day9", "enum", title: "Select Day", required: false, multiple: true, width: 4, options: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"]
                if(month9 == "4" || month9 == "6" || month9 == "9" || month9 == "11") input "day9", "enum", title: "Select Day", required: false, multiple: true, width: 4, options: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30"]
                if(month9 == "2") input "day9", "enum", title: "Select Day", required: true, multiple: true, width: 4, options: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28"]
                if(month9) input "reminder9", "text", title: "Event", required: false, multiple: false, defaultValue: "", width: 4
                if(month9) paragraph "Use this option to override lighting, turn off alarms and more!<br><small>* Automaticaly turns 'on' on the selected day(s), else turns off</small>"
                if(month9) input "dControl9", "capability.switch", title: "Turn this Switch on/off based on the day", submitOnChange: true, multiple: false
                paragraph getFormat("line")
            }
            if(month9) {
                input "month10", "enum", title: "Select Month", required: false, multiple: false, width: 4, submitOnChange: true, options: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"]
                if(month10 == "1" || month10 == "3" || month10 == "5" || month10 == "7" || month10 == "8" || month10 == "10" || month10 == "12") input "day10", "enum", title: "Select Day", required: false, multiple: true, width: 4, options: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"]
                if(month10 == "4" || month10 == "6" || month10 == "9" || month10 == "11") input "day10", "enum", title: "Select Day", required: false, multiple: true, width: 4, options: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30"]
                if(month10 == "2") input "day10", "enum", title: "Select Day", required: true, multiple: true, width: 4, options: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28"]
                if(month10) input "reminder10", "text", title: "Event", required: false, multiple: false, defaultValue: "", width: 4
                if(month10) paragraph "Use this option to override lighting, turn off alarms and more!<br><small>* Automaticaly turns 'on' on the selected day(s), else turns off</small>"
                if(month10) input "dControl10", "capability.switch", title: "Turn this Switch on/off based on the day", submitOnChange: true, multiple: false
            }
		}
        
        section(getFormat("header-green", "${getImage("Blank")}"+" Long Range Dates")) {
            paragraph "For use with dates that are over a year out."
            
            input "longMonth", "enum", title: "Select Month", required: false, multiple: false, width: 4, submitOnChange: true, options: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"]
			if(longMonth == "1" || longMonth == "3" || longMonth == "5" || longMonth == "7" || longMonth == "8" || longMonth == "10" || longMonth == "12") input "longDay", "enum", title: "Select Day", required: false, multiple: true, width: 4, options: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"]
			if(longMonth == "4" || longMonth == "6" || longMonth == "9" || longMonth == "11") input "longDay", "enum", title: "Select Day", required: false, multiple: true, width: 4, options: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30"]
			if(longMonth == "2") input "longDay", "enum", title: "Select Day", required: true, multiple: true, width: 4, options: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28"]
            if(longMonth) input "longYear", "text", title: "Select Year", required: true, multiple: false, defaultValue: "", width: 4
            
			if(longMonth) input "longReminder", "text", title: "Event", required: false, multiple: false, defaultValue: ""
            if(longMonth) paragraph "Use this option to override lighting, turn off alarms and more!<br><small>* Automaticaly turns 'on' on the selected day(s), else turns off</small>"
            if(longMonth) input "dControlL", "capability.switch", title: "Turn this Switch on/off based on the day", submitOnChange: true, multiple: false
        }
        
        section(getFormat("header-green", "${getImage("Blank")}"+" Text Color Options")) {
            paragraph "When date is getting close, the text color can be changed so it stands out.<br>ie. Black, Blue, Brown, Green, Orange, Red, Yellow, White, etc."
			input "sevenDayColor", "text", title: "Seven Days Out", required: true, defaultValue: "Green", width:4
            input "threeDayColor", "text", title: "Three Days Out", required: true, defaultValue: "Orange", width:4
            input "theDayColor", "text", title: "The Days Of", required: true, defaultValue: "Red", width:4
		}
        
		section(getFormat("header-green", "${getImage("Blank")}"+" When to Run")) {
			input "timeToRun", "time", title: "Check daily at", required: true
		}
		section(getFormat("header-green", "${getImage("Blank")}"+" Notification Options")) {
			input "sendPushMessage", "capability.notification", title: "Send a notification?", multiple: true, required: false
		}
		section(getFormat("header-green", "${getImage("Blank")}"+" Dashboard Tile")) {}
		section("Instructions for Dashboard Tile:", hideable: true, hidden: true) {
			paragraph "<b>Want to be able to view your data on a Dashboard? Now you can, simply follow these instructions!</b>"
			paragraph " - Create a new 'Virtual Device' using our 'Simple Dates Driver'.<br> - Then select this new device below.<br> - Now all you have to do is add this device to any of your dashboards to see your data on a tile!"
            paragraph "- Example: I have 3 child apps/virtual devices for dates...<br> - Simple Dates - Holidays<br> - Simple Dates - Special<br> - Simple Dates - School Days Off"
		}
		section() {
			input "tileDevice", "capability.actuator", title: "Vitual Device created to send the data to:", submitOnChange: true, required: false, multiple: false
        }
		section(getFormat("header-green", "${getImage("Blank")}"+" General")) {label title: "Enter a name for this automation", required: false}
        section() {
            input "dateType", "bool", title: "Date Format - <b>off</b>: MM/DD/YYYY - <b>on</b>: DD/MM/YYYY", defaultValue:false, submitOnChange:true
            input "logEnable", "bool", defaultValue: false, title: "Enable Debug Logging", description: "debugging"
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
    startTheProcess()
	initialize()
}

def initialize() {
    setVersion()
    setDefaults()
	schedule(timeToRun, startTheProcess)
}
	
def startTheProcess() {
	createMaps()
    turnDevicesOff()
	
	def nowDate = new Date().format('MM/dd/yyyy', location.timeZone)
	Date todayDate = Date.parse('MM/dd/yyyy', nowDate).clearTime()
	
	state.theReminderMap = state.reminderMap.sort { a, b -> a.key <=> b.key }
	
	state.theReminderMap1S = ""
	state.theReminderMap2S = ""
	state.reminderMapPhoneS = ""
	state.theReminderMap1S = "<table width='100%'>"
	state.theReminderMap2S = "<table width='100%'>"
	
	state.theReminderMap1S += "<tr><td width='8%'><b>Date</b></td><td width='2%'> </td><td width='80%'><b>Reminder</b></td><td width='10%'><b>Days</b></td></tr>"
	state.theReminderMap2S += "<tr><td width='8%'><b>Date</b></td><td width='2%'> </td><td width='80%'><b>Reminder</b></td><td width='10%'><b>Days</b></td></tr>"
	
	if(state.theReminderMap) {
		state.count = 0
		state.reminderCount = 0
		state.theReminderMap.each { it -> 
			//if(logEnable) log.debug "In startTheProcess - Building Table with ${it.key}"
			def futureDate = it.key
			def theEvent = it.value.split(":")
            
            def dControl = theEvent[1]

			state.count = state.count + 1
			state.reminderCount = state.reminderCount + 1
			
			def daysLeft = TimeCategory.minus(futureDate, todayDate)
			def daysLeft1 = daysLeft.days
			if(daysLeft1 >= 0) {
				if(logEnable) log.debug "In startTheProcess - count: ${state.count} - theEvent[0]: ${theEvent[0]} - daysLeft: ${daysLeft1}"
				if(daysLeft1 >= 8) daysLeft2 = "${daysLeft1}"
                if(daysLeft1 <= 7 && daysLeft1 >= 4) daysLeft2 = "<div style='color: ${sevenDayColor};'><b>${daysLeft1}</b></div>"
				if(daysLeft1 <= 3 && daysLeft1 >= 1) daysLeft2 = "<div style='color: ${threeDayColor};'><b>${daysLeft1}</b></div>"
                
                if(logEnable) log.debug "In startTheProcess - dControl: ${dControl}"
                if(daysLeft1 == 0) {
                    daysLeft2 = "<div style='color: ${theDayColor};'><b>Today!</b></div>"
                    if(dControl == "1") dControl1.on()
                    if(dControl == "2") dControl2.on()
                    if(dControl == "3") dControl3.on()
                    if(dControl == "4") dControl4.on()
                    if(dControl == "5") dControl5.on()
                    if(dControl == "6") dControl6.on()
                    if(dControl == "7") dControl7.on()
                    if(dControl == "8") dControl8.on()
                    if(dControl == "9") dControl9.on()
                    if(dControl == "10") dControl10.on()
                    if(dControl == "L") dControlL.on()
                }
			
				nfDate = futureDate.getDateString()
                
                def dateFormat = nfDate.split("/")
                if(!dateType) { fDate = "${dateFormat[0]}/${dateFormat[1]}/${dateFormat[2]}" }
                if(dateType) { fDate = "${dateFormat[1]}/${dateFormat[0]}/${dateFormat[2]}" }

				if((state.count >= 1) && (state.count <= 5)) state.theReminderMap1S += "<tr><td width='8%'>${fDate}</td><td width='2%'> </td><td width='80%'>${theEvent[0]}</td><td width='10%'>${daysLeft2}</td></tr>"
				if((state.count >= 6) && (state.count <= 10)) state.theReminderMap2S += "<tr><td width='8%'>${fDate}</td><td width='2%'> </td><td width='80%'>${theEvent[0]}</td><td width='10%'>${daysLeft2}</td></tr>"
				state.reminderMapPhoneS += "${fDate} - ${theEvent[0]} - ${daysLeft1} \n"
            }
		}
	} else {
		if(state.theReminderMap == null) state.theReminderMap1S = " Nothing to display"
	}	
	state.theReminderMap1S += "</table>"
	state.theReminderMap2S += "</table>"
	def rightNow = new Date()
	state.reminderMapPhoneS += "Report generated: ${rightNow} \n"	
	
	if(logEnable) log.debug "${state.theReminderMap1S}"
	if(logEnable) log.debug "${state.theReminderMap2S}"
	
    if(tileDevice) {
        if(logEnable) log.debug "Sending maps to ${tileDevice}"
	    tileDevice.sendSimpleReminder1(state.theReminderMap1S)
	    tileDevice.sendSimpleReminder2(state.theReminderMap2S)
    }
	if(sendPushMessage) pushHandler()
}

def createMaps(){
    Calendar calendar = new GregorianCalendar()
    int currentYear = calendar.get(Calendar.YEAR)
    def nowDate = new Date().format('MM/dd/yyyy', location.timeZone)        
	Date todayDate = Date.parse('MM/dd/yyyy', nowDate).clearTime()
        
    if(logEnable) log.debug "In createMaps - Year: ${currentYear}"
    
	state.reminderMap = [:]
	state.reminderMapPhoneS = [:]
	
	if(month1) {
        day1.each { it1 ->
		    Date futureDate1 = Date.parse('MM/dd/yyyy', "${month1}/${it1}/${currentYear}").clearTime()
            def daysLeft1 = TimeCategory.minus(futureDate1, todayDate)
		    def daysLeft1a = daysLeft1.days
            if(logEnable) log.debug "In createMaps - Event1 - Days Left: ${daysLeft1a}"
            if(daysLeft1a < 0) {
                currentYear1 = currentYear + 1
                Date futureDate1a = Date.parse('MM/dd/yyyy', "${month1}/${it1}/${currentYear1}").clearTime()
                
                reminder = "${reminder1}:1"
                state.reminderMap.put(futureDate1a, reminder)
            } else{
                reminder = "${reminder1}:1"
		        state.reminderMap.put(futureDate1, reminder)
            }
        }
	}
	if(month2) {
        day2.each { it2 ->
		    Date futureDate2= Date.parse('MM/dd/yyyy', "${month2}/${it2}/${currentYear}").clearTime()
            def daysLeft2 = TimeCategory.minus(futureDate2, todayDate)
		    def daysLeft2a = daysLeft2.days
            if(logEnable) log.debug "In createMaps - Event2 - Days Left: ${daysLeft2a}"
            if(daysLeft2a < 0) {
                currentYear2 = currentYear + 1
                Date futureDate2a = Date.parse('MM/dd/yyyy', "${month2}/${it2}/${currentYear2}").clearTime()
                reminder = "${reminder2}:2"
                state.reminderMap.put(futureDate2a, reminder)
            } else{
                reminder = "${reminder2}:2"
		        state.reminderMap.put(futureDate2, reminder)
            }
        }
	}
	if(month3) {
        day3.each { it3 ->
		    Date futureDate3 = Date.parse('MM/dd/yyyy', "${month3}/${it3}/${currentYear}").clearTime()
            def daysLeft3 = TimeCategory.minus(futureDate3, todayDate)
		    def daysLeft3a = daysLeft3.days
            if(logEnable) log.debug "In createMaps - Event3 - Days Left: ${daysLeft3a}"
            if(daysLeft3a < 0) {
                currentYear3 = currentYear + 1
                Date futureDate3a = Date.parse('MM/dd/yyyy', "${month3}/${it3}/${currentYear3}").clearTime()
                reminder = "${reminder3}:3"
                state.reminderMap.put(futureDate3a, reminder)
            } else{
                reminder = "${reminder3}:3"
		        state.reminderMap.put(futureDate3, reminder)
            }
        }
	}
	if(month4) {
        day4.each { it4 ->
		Date futureDate4 = Date.parse('MM/dd/yyyy', "${month4}/${it4}/${currentYear}").clearTime()
            def daysLeft4 = TimeCategory.minus(futureDate4, todayDate)
		    def daysLeft4a = daysLeft4.days
            if(logEnable) log.debug "In createMaps - Event4 - Days Left: ${daysLeft4a}"
            if(daysLeft4a < 0) {
                currentYear4 = currentYear + 1
                Date futureDate4a = Date.parse('MM/dd/yyyy', "${month4}/${it4}/${currentYear4}").clearTime()
                reminder = "${reminder4}:4"
                state.reminderMap.put(futureDate4a, reminder)
            } else{
                reminder = "${reminder4}:4"
		        state.reminderMap.put(futureDate4, reminder)
            }
        }
	}
	if(month5) {
        day5.each { it5 ->
	    	Date futureDate5 = Date.parse('MM/dd/yyyy', "${month5}/${it5}/${currentYear}").clearTime()
            def daysLeft5 = TimeCategory.minus(futureDate5, todayDate)
	    	def daysLeft5a = daysLeft5.days
            if(logEnable) log.debug "In createMaps - Event5 - Days Left: ${daysLeft5a}"
            if(daysLeft5a < 0) {
                currentYear5 = currentYear + 1
                Date futureDate5a = Date.parse('MM/dd/yyyy', "${month5}/${it5}/${currentYear5}").clearTime()
                reminder = "${reminder5}:5"
                state.reminderMap.put(futureDate5a, reminder)
            } else{
                reminder = "${reminder5}:5"
		        state.reminderMap.put(futureDate5, reminder)
            }
        }
	}
	if(month6) {
        day6.each { it6 ->
		    Date futureDate6 = Date.parse('MM/dd/yyyy', "${month6}/${it6}/${currentYear}").clearTime()
            def daysLeft6 = TimeCategory.minus(futureDate6, todayDate)
		    def daysLeft6a = daysLeft6.days
            if(logEnable) log.debug "In createMaps - Event6 - Days Left: ${daysLeft6a}"
            if(daysLeft6a < 0) {
                currentYear6 = currentYear + 1
                Date futureDate6a = Date.parse('MM/dd/yyyy', "${month6}/${it6}/${currentYear6}").clearTime()
                reminder = "${reminder6}:6"
                state.reminderMap.put(futureDate6a, reminder)
            } else{
                reminder = "${reminder6}:6"
		        state.reminderMap.put(futureDate6, reminder)
            }
        }
	}
	if(month7) {
        day7.each { it7 ->
		    Date futureDate7 = Date.parse('MM/dd/yyyy', "${month7}/${it7}/${currentYear}").clearTime()
            def daysLeft7 = TimeCategory.minus(futureDate7, todayDate)
		    def daysLeft7a = daysLeft7.days
            if(logEnable) log.debug "In createMaps - Event7 - Days Left: ${daysLeft7a}"
            if(daysLeft7a < 0) {
                currentYear7 = currentYear + 1
                Date futureDate7a = Date.parse('MM/dd/yyyy', "${month7}/${it7}/${currentYear7}").clearTime()
                reminder = "${reminder7}:7"
                state.reminderMap.put(futureDate7a, reminder)
            } else{
                reminder = "${reminder7}:7"
		        state.reminderMap.put(futureDate7, reminder)
            }
        }
	}
	if(month8) {
        day8.each { it8 ->
		    Date futureDate8 = Date.parse('MM/dd/yyyy', "${month8}/${it8}/${currentYear}").clearTime()
            def daysLeft8 = TimeCategory.minus(futureDate8, todayDate)
		    def daysLeft8a = daysLeft8.days
            if(logEnable) log.debug "In createMaps - Event8 - Days Left: ${daysLeft8a}"
            if(daysLeft8a < 0) {
                currentYear8 = currentYear + 1
                Date futureDate8a = Date.parse('MM/dd/yyyy', "${month8}/${it8}/${currentYear8}").clearTime()
                reminder = "${reminder8}:8"
                state.reminderMap.put(futureDate8a, reminder)
            } else{
                reminder = "${reminder8}:8"
		        state.reminderMap.put(futureDate8, reminder)
            }
        }
	}
	if(month9) {
        day9.each { it9 ->
		    Date futureDate9 = Date.parse('MM/dd/yyyy', "${month9}/${it9}/${currentYear}").clearTime()
            def daysLeft9 = TimeCategory.minus(futureDate9, todayDate)
		    def daysLeft9a = daysLeft9.days
            if(logEnable) log.debug "In createMaps - Event9 - Days Left: ${daysLeft9a}"
            if(daysLeft9a < 0) {
                currentYear9 = currentYear + 1
                Date futureDate9a = Date.parse('MM/dd/yyyy', "${month9}/${it9}/${currentYear9}").clearTime()
                reminder = "${reminder9}:9"
                state.reminderMap.put(futureDate9a, reminder)
            } else{
                reminder = "${reminder9}:9"
	    	    state.reminderMap.put(futureDate9, reminder)
            }
        }
	}
	if(month10) {
        day10.each { it10 ->
		    Date futureDate10 = Date.parse('MM/dd/yyyy', "${month10}/${it10}/${currentYear}").clearTime()
            def daysLeft10 = TimeCategory.minus(futureDate10, todayDate)
		    def daysLeft10a = daysLeft10.days
            if(logEnable) log.debug "In createMaps - Event10 - Days Left: ${daysLeft10a}"
            if(daysLeft10a < 0) {
                currentYear10 = currentYear + 1
                Date futureDate10a = Date.parse('MM/dd/yyyy', "${month10}/${it10}/${currentYear10}").clearTime()
                reminder = "${reminder10}:10"
                state.reminderMap.put(futureDate10a, reminder)
            } else{
                reminder = "${reminder10}:10"
		        state.reminderMap.put(futureDate10, reminder)
            }
        }
	}
    
    if(longMonth) {
        longDay.each { it ->
		    Date futureDate = Date.parse('MM/dd/yyyy', "${longMonth}/${it}/${longYear}").clearTime()
            def daysLeft = TimeCategory.minus(futureDate, todayDate)
		    def daysLefta = daysLeft.days
            if(logEnable) log.debug "In createMaps - LongEvent - Days Left: ${daysLefta}"
            if(daysLefta < 0) {
                state.reminderMap.remove(futureDate)
            } else {
                reminder = "$longReminder:L"
		        state.reminderMap.put(futureDate, reminder)
            }
        }
	}
}

def turnDevicesOff() {
    if(logEnable) log.debug "In turnDevicesOff (${state.version})"
    if(dControl1) dControl1.off()
    if(dControl2) dControl2.off()
    if(dControl3) dControl3.off()
    if(dControl4) dControl4.off()
    if(dControl5) dControl5.off()
    if(dControl6) dControl6.off()
    if(dControl7) dControl7.off()
    if(dControl8) dControl8.off()
    if(dControl9) dControl9.off()
    if(dControl10) dControl10.off()
    if(dControlL) dControl10.off()
}

def pushHandler(){
	if(logEnable) log.debug "In pushNow (${state.version})"
	theMessage = "${state.reminderMapPhoneS}"
	if(logEnable) log.debug "In pushNow...Sending message: ${theMessage}"
   	sendPushMessage.deviceNotification(theMessage)
}

def getDateTime() {
	def date = new Date()
	if(dateType == false) newdate=date.format("MM-d HH:mm")
	if(dateType == true) newdate=date.format("MM-d hh:mm")
    return newdate
}

// ********** Normal Stuff **********

def setDefaults(){
	if(logEnable == null){logEnable = false}
    if(dateType == null){dateType = false}
	if(state.msg == null){state.msg = ""}
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
