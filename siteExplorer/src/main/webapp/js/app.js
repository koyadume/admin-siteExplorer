$(function() {
	//events for sites.xml page
	/* $('.pseudo-new-site').click(function() {
		var form = createPluginForm(${this}, 'siteDetails'); 
    	 form.appendTo('.pseudo-tile-content');
    	 form.submit();
    });
    
    $('.pseudo-edit-site').click(function() {
    	var form = createPluginForm(${this}, 'siteDetails'); 
    	 createHidden('id', $(this).attr('id')).appendTo(form);
    	 form.appendTo('.pseudo-tile-content');
    	 form.submit();
    }); */
    
    //events for siteNodes.xml page
	$('#site').change(function() { 
        var siteId = $('#site').val();
        if(siteId != "") {
        	var form = createPluginForm($(this), 'getSitePageChildren');
        	createHidden('siteId', siteId).appendTo(form);
        	form.appendTo('.pseudo-tile-content');
        	form.submit();
        } else {
			$('#resChildren').html('');				        
        }
    });
    
    $('.pseudo-pgLink').click(function() {
    	var siteId = $('#site').val();
   		var form = createPluginForm($(this), 'getSitePageChildren');
   		createHidden('siteId', siteId).appendTo(form);
   		createHidden('parentId', $(this).attr('id')).appendTo(form);
   		form.appendTo('.pseudo-tile-content');
   		form.submit();
    });
    
     /* $('.pseudo-new-page').click(function() {
    	 var form = createPluginForm(${this}, 'pageDetails'); 
     	 form.appendTo('.pseudo-tile-content');
     	 form.submit();
     });
     
     $('.pseudo-edit-page').click(function() {
     	 var form = createPluginForm(${this}, 'pageDetails'); 
     	 createHidden('id', $(this).attr('id')).appendTo(form);
     	 form.appendTo('.pseudo-tile-content');
     	 form.submit();
     }); */
     
     function move(ele, action) {
		// create form
     	var form = createPluginForm(ele, 'movePage');
     	setPluginNextAction(form, 'getSitePageChildren');
     	// set hidden parameters
     	createHidden('pageId', ele.attr('id'));
     	createHidden('action', action);
     	// submit form
   		form.appendTo('.pseudo-tile-content');
   		form.submit();
     }
     
     $('.pseudo-moveDown').click(function() {
   		move($(this), 'down');
     });
    
     $('.pseudo-moveUp').click(function() {
		move($(this), 'up');
     });
});