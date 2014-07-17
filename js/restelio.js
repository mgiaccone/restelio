Restelio = function() {

};

Restelio.prototype.init = function() {
    this.switchApiVersion($("#rio-javadoc-select"));
};

/**
 * Javadoc version selector handling
 * @param el The select element
 */
Restelio.prototype.switchApiVersion = function(el) {
    if (el) {
        $("#rio-javadoc-iframe").attr("src", $(el).val());
    }
};

// closure compiler externs
window['Restelio'] = Restelio; // <-- Constructor
Restelio.prototype['init'] = Restelio.prototype.init;
Restelio.prototype['switchApiVersion'] = Restelio.prototype.switchApiVersion;

// register instance on load
var restelio = restelio || new Restelio();
window['restelio'] = restelio;
