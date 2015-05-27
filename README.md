## USSDParser
This application gets the USSD text responses from a given number

###Description
After 2 weeks of research, I've found 2 solutions for intercept the USSD text  listed below. Compatibility with Android 2.3 - 4.2.1 because Mr. Google drops supports of USSD api in later versions.

**Solutions:**

1. Extend the service IExtendedNetworkService and intercept the USSD text
2. Read the logcat of the phone and search the USSD text then parse it

I did use the second approach for make this application.

### General Instructions:
1. Type a USSD Code
2. Press Call button 
3. Enjoy 

The time interval, start service and stop service buttons, aren't necessaries for get the USSD response, feel free to remove this buttons.

### Support

Sergio Peralta serpel.js@gmail.com

Please file issues [click here] at Github. 

Copyright (c) 2015 Sergio Peralta. This software is licensed under the MIT License.

Good luck!

[click here]:https://github.com/serpel/USSDParser/issues

### Fork it

- Create your feature branch (git checkout -b my-new-feature)
- Commit your changes (git commit -am 'Add some feature')
- Push to the branch (git push origin my-new-feature)
- Create a new Pull Request
