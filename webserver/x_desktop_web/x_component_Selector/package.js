MWF.xApplication.Selector = MWF.xApplication.Selector || {};
MWF.xDesktop.requireApp("Selector", "lp."+MWF.language, null, false);
//MWF.xDesktop.requireApp("Selector", "Actions.RestActions", null, false);
MWF.O2Selector = new Class({
    Implements: [Options],
    options: {
        "count": 0,
        "type": "person",
        "title": "Select Person",
        "groups": [],
        "roles": [],
        "units": [],
        "unitType": "",
        "values": []
    },
    initialize: function(container, options){
        //MWF.xDesktop.requireApp("Selector", "Actions.RestActions", null, false);
        this.setOptions(options);
        this.container = container;
        var type = this.options.type.capitalize();

        if (type){
            if ((type.toLowerCase()==="unit") && (this.options.unitType)){
                MWF.xDesktop.requireApp("Selector", "UnitWithType", function(){
                    this.selector = new MWF.xApplication.Selector.UnitWithType(this.container, options);
                    this.selector.load();
                }.bind(this));
            }else if ((type.toLowerCase()==="identity") && ((this.options.dutys) && this.options.dutys.length)){
                MWF.xDesktop.requireApp("Selector", "IdentityWidthDuty", function(){
                    this.selector = new MWF.xApplication.Selector.IdentityWidthDuty(this.container, options);
                    this.selector.load();
                }.bind(this));
            }else{
                debugger;
                MWF.xDesktop.requireApp("Selector", type, function(){
                    this.selector = new MWF.xApplication.Selector[type](this.container, options);
                    this.selector.load();
                }.bind(this));
            }
        }else{
            debugger;
            MWF.xDesktop.requireApp("Selector", "MultipleSelector", function() {
                this.selector = new MWF.xApplication.Selector.MultipleSelector(this.container, this.options );
                this.selector.load();
            }.bind(this));
        }
    }
});

MWF.O2SelectorFilter = new Class({
    Implements: [Options],
    options: {
        "count": 0,
        "type": "person",
        "title": "Select Person",
        "groups": [],
        "roles": [],
        "units": [],
        "unitType": "",
        "values": []
    },
    initialize: function(value, options){
        //MWF.xDesktop.requireApp("Selector", "Actions.RestActions", null, false);
        this.setOptions(options);
        this.value = value;
        var type = this.options.type.capitalize();

        if (type){
            if ((type.toLowerCase()==="unit") && (this.options.unitType)){
                MWF.xDesktop.requireApp("Selector", "UnitWithType", function(){
                    this.selector = new MWF.xApplication.Selector.UnitWithType.Filter(this.container, options);
                    this.selector.load();
                }.bind(this));
            }else if ((type.toLowerCase()==="identity") && ((this.options.dutys) && this.options.dutys.length)){
                MWF.xDesktop.requireApp("Selector", "IdentityWidthDuty", function(){
                    this.selectFilter = new MWF.xApplication.Selector.IdentityWidthDuty.Filter(this.value, options);
                }.bind(this), false);
            }else{
                MWF.xDesktop.requireApp("Selector", type, function(){
                    this.selectFilter = new MWF.xApplication.Selector[type].Filter(this.value, options);
                }.bind(this), false);
             }
        }else{
            debugger;
            MWF.xDesktop.requireApp("Selector", "MultipleSelector", function() {
                this.selectFilter = new MWF.xApplication.Selector.MultipleSelector.Filter(this.container, this.options );
            }.bind(this), false);
        }

    },
    filter: function(value, callback){
        return this.selectFilter.filter(value, callback);
    }

});