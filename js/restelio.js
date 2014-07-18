Restelio = function() {

};

Restelio.prototype.animationDuration = 400;
Restelio.prototype.navbarHeight = 0;
Restelio.prototype.breadcrumbHeight = 0;
Restelio.prototype.headerHeight = 0;

Restelio.prototype.pageWrapperElement;
Restelio.prototype.drawerElement;
Restelio.prototype.drawerToggleElement;

Restelio.prototype.init = function() {
    this.drawerElement = $("#rio-drawer-menu");
    this.drawerToggleElement = $("#rio-drawer-toggle");
    this.pageWrapperElement = $("#rio-page-wrapper");

    // Make the page wrapper to always fill the rest of the screen
    $(window).resize(function(){
        restelio.onWindowResize();
    });
    this.onWindowResize();

    // Force selection of default javadoc page
    this.switchApiVersion($("#rio-javadoc-select"));

    // Event listeners
    $(document).on('click touchend', this.drawerToggleElement.selector, this.toggleDrawer);
};

/**
 * Handle window resize
 */
Restelio.prototype.onWindowResize = function() {
    this.breadcrumbHeight = $("#rio-breadcrumb").outerHeight();
    this.navbarHeight = $("#rio-navbar").outerHeight();
    this.headerHeight = this.navbarHeight + this.breadcrumbHeight;
    this.resizePageWrapper();
    this.resizeDrawer();
};

Restelio.prototype.toggleDrawer = function(e) {
    e.preventDefault();
    e.stopPropagation();

    var drawerEl = restelio.drawerElement; //$("#rio-drawer-menu");
    if (drawerEl.hasClass('rio-drawer-open')) {
        // Close
        drawerEl.removeClass('rio-drawer-open')
            .stop(true)
            .animate({ left : '-100%' }, restelio.animationDuration);

        $('.rio-drawer-overlay').fadeOut(restelio.animationDuration, function() {
            $(document).off('click touchend', '.rio-drawer-overlay');
            $('.rio-drawer-overlay').remove();
        });
    } else {
        // Open
        var overlay = $('<div class="rio-drawer-overlay"></div>')
        overlay.insertBefore(drawerEl);
        overlay.fadeIn(restelio.animationDuration, function() {});

        drawerEl.addClass('rio-drawer-open')
            .stop(true)
            .animate({ left : 0 }, restelio.animationDuration, function() {
                console.log('Added overlay click event');
                $(document).on('click touchend', '.rio-drawer-overlay', restelio.toggleDrawer);
            });

        restelio.resizeDrawer();

    }
}

/**
 * Javadoc version selector handling
 * @param el The select element
 */
Restelio.prototype.switchApiVersion = function(el) {
    if (el) {
        $("#rio-javadoc-iframe").attr("src", $(el).val());
    }
};

Restelio.prototype.resizePageWrapper = function() {
    this.resizeElementToFillPage(this.pageWrapperElement, this.headerHeight);
    var marginTop = (this.breadcrumbHeight == 0) ? this.headerHeight : 0;
    this.pageWrapperElement.css('margin-top', marginTop + 'px');
}

Restelio.prototype.resizeDrawer = function() {
    var elDrawer = this.drawerElement;
    elDrawer.css('left', '-' + $(window).width() + 'px');
    this.resizeElementToFillPage(elDrawer, this.navbarHeight);

    var elOverlay = $(".rio-drawer-overlay");
    console.log('got drawer overlay [' + elOverlay.length + ']');
    if (elOverlay.length) {
        console.log('resizing drawer overlay');
        this.resizeElementToFillPage(elOverlay, this.navbarHeight);
    }
}

Restelio.prototype.resizeElementToFillPage = function(el, top) {
    if (el) {
        var marginTop = (top || 0);
        var h = $(window).height() - marginTop;
        $(el).height(h);
        console.log('element size [h=' + $(el).outerHeight() + ', w=' + el.outerWidth() + ', top=' + marginTop + ']');
    }
};

// Closure compiler externs
window['Restelio'] = Restelio; // <-- Constructor

// Public methods
Restelio.prototype['init'] = Restelio.prototype.init;
Restelio.prototype['resizeElementToFillPage'] = Restelio.prototype.resizeElementToFillPage;
Restelio.prototype['switchApiVersion'] = Restelio.prototype.switchApiVersion;

// Register instance on load
var restelio = restelio || new Restelio();
window['restelio'] = restelio;
