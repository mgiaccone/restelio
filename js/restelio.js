Restelio = function() {

};

Restelio.prototype.init = function() {
//    alert("Initializing restelio");
//    this.navbarFading();
};

Restelio.prototype.navbarFading = function() {
    alert("Navbar");
};

// closure compiler externs
window['Restelio'] = Restelio; // <-- Constructor
Restelio.prototype['init'] = Restelio.prototype.init;

// register instance on load
var restelio = restelio || new Restelio();
window['restelio'] = restelio;
