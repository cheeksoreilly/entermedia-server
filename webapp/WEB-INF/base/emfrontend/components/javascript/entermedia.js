var ajaxtimerrunning = false;

repaint = function(divid) {
	var div = jQuery("#" + divid);
	var href = div.data('href');
	var args = div.data('args');
	jQuery.get(href + "?" + args, {}, function(data) 
			{
				//var toreplace = jQuery("#" + targetDiv);
				div.replaceWith(data);
			}
	);
}

toggleUserProperty = function(property, onsuccess) {
	jQuery.ajax(
			{
				url: "${home}${apphome}/components/userprofile/toggleprofileproperty.html?field=" + property,
				success: onsuccess
			}
		);
}

setUserProperty = function(property, value, onsuccess) {
	/*jQuery.get(
			url: "${apphome}/components/userprofile.html",
			{field: property, property + ".value": value},
			success: onsuccess
		);*/
}

outlineSelectionCol = function(event, ui)
{
	jQuery(this).addClass("headerdraggableenabled");
}
	
unoutlineSelectionCol = function(event, ui)
{
	jQuery(this).removeClass("headerdraggableenabled");
}

outlineSelectionRow = function(event, ui)
{
	jQuery(this).addClass("rowdraggableenabled");
}
	
unoutlineSelectionRow = function(event, ui)
{
	jQuery(this).removeClass("rowdraggableenabled");
}

toggleajax = function(e) 
{
	e.preventDefault();
	var nextpage= jQuery(this).attr('href');
	var loaddiv = jQuery(this).attr("targetdivinner");
	loaddiv = loaddiv.replace(/\//g, "\\/");
	var cell = jQuery("#" + loaddiv);

	if ( cell.hasClass("toggle_on") || cell.hasClass("toggle_off") ) 
	{
		var off = cell.hasClass("toggle_off");
		if (off) 
		{
			cell.removeClass("toggle_off");
			cell.addClass("toggle_on");
			cell.show('fast');
		}
		else
		{	
			cell.removeClass("toggle_on");
			cell.addClass("toggle_off");
			cell.hide('fast');
		}
	}
	else
	{
		jQuery.get(nextpage, {}, function(data) 
			{
				cell.html(data);
				cell.addClass("toggle_on");
				cell.show('fast');
			}
		);
	}
}

runajax = function(e)
{
	var nextpage= jQuery(this).attr('href');
	
	var targetDiv = jQuery(this).attr("targetdiv");
	if( targetDiv)
	{
		targetDiv = targetDiv.replace(/\//g, "\\/");
		jQuery.get(nextpage, {}, function(data) 
			{
				var cell = jQuery("#" + targetDiv);
				cell.replaceWith(data);
				
				
				if (typeof(doResize) == "function")
				{
					doResize();
				}
				
			}
		);
	}
	else
	{
		var loaddiv = jQuery(this).attr("targetdivinner");
		loaddiv = loaddiv.replace(/\//g, "\\/");
		//jQuery("#"+loaddiv).load(nextpage);
		jQuery.get(nextpage, {}, function(data) 
				{
					var cell = jQuery("#" + loaddiv);
					cell.html(data);
					if (typeof(doResize) == "function")
					{
						doResize();
					}
				}

			);

	}
     e.preventDefault();
	//return false;
}

showHoverMenu = function(inDivId)
{
	el = jQuery("#" + inDivId);
	if( el.attr("status") == "show")
	{
		el.show();
	}
}



toggleitem = function(e)
{
		
		var nextpage= jQuery(this).attr('href');
		var targetDiv = jQuery(this).attr("targetdiv");
		targetDiv = targetDiv.replace(/\//g, "\\/");
		
		jQuery("#"+targetDiv).load(nextpage, {}, function()
			{
				var html = jQuery(this).html();
				
				jQuery("."+targetDiv, window.parent.document).html(html);
			    jQuery("#basketmenu").load("$home/$applicationid/components/albums/basket/menuitem.html");
				jQuery("#basketmenu",	$!message
 window.parent.document).load("$home/$applicationid/components/albums/basket/menuitem.html");
				jQuery("#collectionbasket", window.parent.document).load("$home/$applicationid/views/albums/basket/index.html?oemaxlevel=1");
						
				

			}
		);

		return false;

}



updatebasket = function(e)
{
		
		var nextpage= jQuery(this).attr('href');
		var targetDiv = jQuery(this).attr("targetdiv");
		targetDiv = targetDiv.replace(/\//g, "\\/");
		var action= jQuery(this).data('action');
		jQuery("#"+targetDiv).load(nextpage, {}, function()
			{
				
				
			    jQuery("#basket-paint").load("$home/$applicationid/components/basket/menuitem.html");
				if(action == 'remove'){
					jQuery(".selectionbox:checked").closest("tr").hide("slow");
				}
						
				

			}
		);

		return false;

}



toggleorderitem = function(e)
{
		
		var nextpage= jQuery(this).attr('href');
		var targetDiv = jQuery(this).attr("targetdiv");
		targetDiv = targetDiv.replace(/\//g, "\\/");
		
		jQuery("."+targetDiv).load(nextpage, {}, function()
			{
				var html = jQuery(this).html();
				
				jQuery("."+targetDiv, window.parent.document).html(html);
			    jQuery("#basketmenu").load("$home/$applicationid/views/activity/menuitem.html");
				jQuery("#basketmenu", window.parent.document).load("$home/$applicationid/views/activity/menuitem.html");
				// jQuery("#collectionbasket",
				// window.parent.document).load("$home/$applicationid/views/albums/basket/index.html?oemaxlevel=1");
						
				

			}
		);

		return false;

}

getConfirmation = function(inText)	$!message

{
	if(!confirm(inText))
	{
		return false;
	}
	return true;
}

clearApplets = function()
{
	// Try to remove all applets before submitting form with jQuery.
	var coll = document.getElementsByTagName("APPLET");
	for (var i = 0; i < coll.length; i++) 
	{
	    var el = coll[i];
	    el.parentNode.removeChild(el);
	} 
}

pageload = function(hash) 
{
	// hash doesn't contain the first # character.
	if(hash) 
	{
		var hasharray = hash.split("|");
		var targetdiv = hasharray[1];
		var location = hasharray[0];
		if(targetdiv != null && location!= null)
		{
			targetdiv = targetdiv.replace(/\//g, "\\/");
			jQuery("#"+targetdiv).load(location);
		}
	} 
}

/*
formatHitCountResult = function(inRow)
{
	return inRow[1];
}

formatHitCount = function(inRow)
{jQuery('select#speedC').selectmenu({style:'dropdown'});
	return inRow[0];
}
*/

// Everyone put your onload stuff in here:
onloadselectors = function()
{
	
	jQuery("a.ajax").livequery('click', runajax);
	jQuery("a.toggleajax").livequery('click', toggleajax);
	
	jQuery("a.updatebasket").livequery('click', updatebasket);
	
	
	jQuery("form.ajaxform").livequery('submit',	
		function() 
		{
			var targetdiv = jQuery(this).attr("targetdiv");
			targetdiv = targetdiv.replace(/\//g, "\\/");
			// allows for posting to a div in the parent from a fancybox.
			if(targetdiv.indexOf("parent.") == 0)
			{
				targetdiv = targetdiv.substr(7);
				parent.jQuery(this).ajaxSubmit({target: "#" + targetdiv});
				// closes the fancybox after submitting
				parent.jQuery.fn.fancybox.close();
			}
			else
			{
				jQuery(this).ajaxSubmit( {target:"#" + targetdiv} );
			}
			return false;
		}
	);
	jQuery("form.ajaxautosubmit").livequery( function() 
			{
				var theform = jQuery(this); 
				theform.find("select").change( function()
						{
							theform.submit();
						});
			});
	jQuery("a.thickbox, a.emdialog").livequery(
		function() 
		{
			jQuery(this).fancybox(
			{ 
				'zoomSpeedIn': 300, 'zoomSpeedOut': 300, 'overlayShow': true,
				enableEscapeButton: true, type: 'iframe'
			});
		}
	); 
	
	jQuery("a.slideshow").livequery(
		function() 
		{
			jQuery(this).fancybox(
			{ 
				'zoomSpeedIn': 300, 'zoomSpeedOut': 300, 'overlayShow': true , 'slideshowtime': 6000
			});
		}
	);

	jQuery("img.framerotator").livequery(
		function()
		{
			jQuery(this).hover(
				function() {
					jQuery(this).data("frame", 0);
					var path = this.sr$('select#speedC').selectmenu({style:'dropdown'});c.split("?")[0];
					var intval = setInterval("nextFrame('" +  this.id + "', '" + path + "')", 1000);
					jQuery(this).data("intval", intval);
				},
				function() {
					var path = this.src.split("?")[0];
					this.src = path + '?frame=0';
					var intval = jQuery(this).data("intval");
					clearInterval(intval);
				}
			); 
		});

	/*
	// Live query not needed since Ajax does not normally replease the header
	// part of a page
	var theinput = jQuery("#assetsearchinput");
	if( theinput && theinput.autocomplete )
	{
		theinput.autocomplete('$home$apphome/components/autocomplete/suggestions.txt', {
			selectFirst: false,
			formatItem: formatHitCount,
			formatResult:formatHitCountResult
		});
	}

	// For group manager area
	jQuery("#useremailinput").livequery( function() 
	{
		var theinput = jQuery(this);
		if( theinput && theinput.autocomplete )
		{
			theinput.autocomplete('$home/$applicationid/components/autocomplete/emailsuggestions.txt', {
				selectFirst: false,
				matchCase: true,
				formatResult:formatHitCountResult
			});
		}
	});
	jQuery("#friendemailinput").livequery( function() 
	{
		var theinput = jQuery(this);
		if( theinput && theinput.autocomplete )
		{
			theinput.autocomplete('$home/$applicationid/components/autocomplete/friendemailsuggestions.txt', {
				selectFirst: false,
				matchCase: true,
				formatResult:formatHitCountResult
			});
		}
	});
	*/
	
	

	jQuery("#assetsearchinput").livequery( function() 
			{
				var theinput = jQuery(this);
				if( theinput && theinput.autocomplete )
				{
					theinput.autocomplete({
						source: '$home$apphome/components/autocomplete/assetsuggestions.txt',
						select: function(event, ui) {
							//set input that's just for display purposes
							theinput.val(ui.item.value);
							theinput.submit();
							return false;
						}
					});
				}
			});

	
	jQuery(".addmygroupusers").livequery( function() 
			{
				var theinput = jQuery(this);
				if( theinput && theinput.autocomplete )
				{
					var assetid = theinput.attr("assetid");
					/*theinput.autocomplete({
					    source: ["c++", "java", "php", "coldfusion", "javascript", "asp", "ruby"]
					});*/
					theinput.autocomplete({
						source: '$home$apphome/components/autocomplete/addmygroupusers.txt?assetid=' + assetid,
						select: function(event, ui) {
							//set input that's just for display purposes
							jQuery(".addmygroupusers").val(ui.item.display);
							//set a hidden input that's actually used when the form is submitted
							jQuery("#hiddenaddmygroupusers").val(ui.item.value);
							var targetdiv = jQuery("#hiddenaddmygroupusers").attr("targetdiv");
							var targeturl = jQuery("#hiddenaddmygroupusers").attr("postpath");
							jQuery.get(targeturl + ui.item.value, 
									function(result) {
										jQuery("#" + targetdiv).html(result);
							});
							return false;
						}
					});
				}
			});
	jQuery(".userautocomplete").livequery( function() 
			{
				var theinput = jQuery(this);
				if( theinput && theinput.autocomplete )
				{
					var theinputhidden = theinput.attr("id") + "hidden";
					theinput.autocomplete({
						source: '$home$apphome/components/autocomplete/usersuggestions.txt',
						select: function(event, ui) {
							//set input that's just for display purposes
							theinput.val(ui.item.display);
							//set a hidden input that's actually used when the form is submitted
							jQuery("#" + theinputhidden).val(ui.item.value);
							return false;
						}
					});
				}
			});

	
	
	jQuery(".addmygroups").livequery( function() 
	{
		var theinput = jQuery(this);
		if( theinput && theinput.autocomplete )
		{
			var assetid = theinput.attr("assetid");
			theinput.autocomplete({
					source:  '$home$apphome/components/autocomplete/addmygroups.txt?assetid=' + assetid,
					select: function(event, ui) {
						//set input that's just for display purposes
						jQuery(".addmygroups").val(ui.item.label);
						//set a hidden input that's actually used when the form is submitted
						jQuery("#hiddenaddmygroups").val(ui.item.value);
						var targetdiv = jQuery("#hiddenaddmygroups").attr("targetdiv");
						var targeturl = jQuery("#hiddenaddmygroups").attr("postpath");
						jQuery.get(targeturl + ui.item.value, 
								function(result) {
									jQuery("#" + targetdiv).html(result);
						});
						return false;
					}
			});
		}
	});
	
	
	jQuery("table.striped tr:nth-child(even)").livequery( function()
		{
			jQuery(this).addClass("odd");
		});
		
	jQuery("div.emtable.striped div.row:nth-child(even)").livequery( function()
		{
			jQuery(this).addClass("odd");
		});
	jQuery("#tree div:even").livequery( function(){
		jQuery(this).addClass("odd");
	});
	jQuery('.commentresizer').livequery( function()
	{	
		var ta = jQuery(this).find("#commenttext");
		ta.click(function() 
		{
			var initial = ta.attr("initialtext");
			if( ta.val() == "Write a comment" ||  ta.val() == initial) 
			{
				ta.val('');
				ta.unbind('click');
				var button = jQuery('.commentresizer #commentsubmit');
				button.show();	
			}
		});
		ta.prettyComments();
		// ta.focus();
	});
	

	var ta = jQuery(".initialtext");
	ta.click(function() 
	{
		var initial = ta.attr("initialtext");
		if( ta.val() == "Write a comment" ||  ta.val() == initial) 
		{
			ta.val('');
			ta.unbind('click');
		}
	});

	
	if( !window.name || window.name == "")
	{
		window.name = "uploader" + new Date().getTime();	
	}
	
	var appletholder = jQuery('#emsyncstatus');
	if(appletholder.size() > 0)
	{
		appletholder.load('$home/${page.applicationid}/components/uploadqueue/index.html?appletname=' + window.name);
	}
	
	jQuery('.baseemshowonhover' ).livequery( function() 
	{ 
		var image = jQuery(this);
		
		jQuery(this).parent().hover(
				function () 
				{
					image.addClass("baseemshowonhovershow");
			 	}, 
				function () {
			 		image.removeClass("baseemshowonhovershow");
				}
			);	
	});
	
	// Handles emdropdowns
	jQuery("div[id='emdropdown']").livequery(
		function()
		{
			jQuery(this).mouseleave(
				function(){
					var el = document.getElementById("emdropdowndiv");
					if( el )
					{
						jQuery(el).attr("status","hide"); // Beware this gets
															// called when popup
															// is shown
					}
				});
		
			jQuery(this).click(
				function()
				{
					var el = jQuery(this).find(".emdropdowncontent");
					el.bind("mouseleave",function()
					{
						jQuery(this).attr("status","hide");
						jQuery(this).hide();
					});
					//var offset = jQuery(this).offset();
					//var top = offset.top + 20;
					//el.css("top",  top + "px");
					//el.css("left", offset.left+ "px"); 
					
					var path = el.attr("contentpath");
					if( path )
					{
						el.load('$home' + path);
					}
					el.attr("status","show"); //The mouse may jump over a gap so we need delay the show
					el.show();
					var id = el.attr('id');
					setTimeout('showHoverMenu(\"' +  id + '")',300)
			});
			
		}
	);
	if( jQuery.history )
	{
		jQuery.history.init(pageload);
		// set onlick event for buttons
		jQuery("a[class='ajax']").click(function()
		{
			var hash = this.href;
			var targetdiv = this.targetdiv;
			
			hash = hash.replace(/^.*#/, '');
			// moves to a new page.
			// page load is called at once.
			hash=hash+"|"+targetdiv;
			jQuery.history.load(hash);
			return false;  // why is this here
		});
	}	

	// This clears out italics and grey coloring from the search box if it has a
	// user-entered value
	if(jQuery("#assetsearchinput").val() != "Search")
	{
		jQuery("#assetsearchinput").removeClass("defaulttext");
	}
	
	jQuery(".baskettoggle").live('click', function(event) {
	    var cbox = jQuery(this);
	    var url = cbox.attr('data-href');
	    var assetid = cbox.attr('data-assetid');
	    
		jQuery("#assettoggle" + assetid).load(url);
		return false;
	});
	
	jQuery('#mattresulttable table tr').live('click',
			function(event) {
				//find the rowid go there
				var id = jQuery(this).attr("rowid");
				window.location = id;
			}
	);

		jQuery('#emselectable table td' ).livequery(	
			function()
			{
				if(jQuery(this).attr("noclick") =="true"){
					return true;
				}
				
				jQuery(this).click(
					function(event) 
					{
						jQuery('#emselectable table tr' ).each(function(index) 
						{ 
							jQuery(this).removeClass("emhighlight");
						});
						var row = jQuery(this).closest("tr");
						jQuery(row).addClass('emhighlight');
						jQuery(row).removeClass("emborderhover");
						
						var id = jQuery(row).attr("rowid");
						var form = jQuery('#emselectable').find("form");
						if( form.length > 0 )
						{
							jQuery('#emselectable #emselectedrow').val(id);
							jQuery("#emselectable .emneedselection").each( function()
								{
									jQuery(this).removeAttr('disabled');
								});	
							form.submit();
						}
						/*
						else if(jQuery('#emselectable #editlink'))
						{
							var tablediv = jQuery(this).parents('.emselectable').filter(':first');
							var targetdiv = tablediv.data('targetdiv');
							var editpath = tablediv.data('editpath');
							targetdiv = targetdiv.replace(/\//g, "\\/");
							var id = jQuery(row).attr("rowid");
							editpath = editpath + "&id=" + id;
							jQuery("#" + targetdiv).load(editpath);
						}
						*/
						else
						{
							window.location = id;
						}
					}
				);		
			}
		);

	jQuery('#emselectable table tr' ).livequery(
	function()
	{
		jQuery(this).hover(
			function () 
			{
			  	var row = jQuery(this).closest("tr");
				var id = jQuery(row).attr("rowid");
			    if( id != null )
			    {
				    jQuery(this).addClass("emborderhover");
				}
		 	}, 
			function () {
			    jQuery(this).removeClass("emborderhover");
			}
		);
	});
		
	
	jQuery(".toggleitem").livequery('click', toggleitem);
	
	jQuery(".toggleorderitem").livequery('click', toggleorderitem);

	jQuery(".headerdraggable").livequery( 
			function()
			{	
				jQuery(this).draggable( 
					{ 
						helper: 'clone',
						revert: 'invalid',
						opacity: '.9'
					}
				);
			}
		);
	jQuery(".rowdraggable").livequery( 
			function()
			{	
				jQuery(this).draggable( 
					{ 
						helper: 'clone',
						revert: 'invalid',
						opacity: '.9'
					}
				);
			}
		);
	jQuery(".assetdraggable").livequery( 
			function()
			{	
				jQuery(this).draggable( 
					{ 
						helper: 'clone',
						revert: 'invalid',
						opacity: '.3'
					}
				);
			}
		);
	
	jQuery(".headerdroppable").livequery(
			function()
			{
				jQuery(this).droppable(
					{
						drop: function(event, ui) {
							var source = ui.draggable.attr("id");
							var destination = this.id;
							
							//searchtype=asset&hitssessionid=$hits.getSessionId()&editheader=true
							
							var sessionid = ui.draggable.attr("hitssessionid");
							
							var editing = ui.draggable.attr("editing")
							if( !editing )
							{
								editing = false;
							}
							jQuery("#resultsdiv").load("$home$apphome/components/results/savecolumns.html",
								{
								"source":source,
								"destination":destination,
								editheader:editing,
								searchtype:"asset",
								"hitssessionid":sessionid
								});
							//ui.helper.effect("transfer", { to: jQuery(this).children("a") }, 200);
						},
						tolerance: 'pointer',
						over: outlineSelectionCol,
						out: unoutlineSelectionCol
					}
				);
			}
		);
	jQuery(".assetdropcategory .treerowtext").livequery(
			function()
			{
				jQuery(this).droppable(
					{
						drop: function(event, ui) {
							var assetid = ui.draggable.attr("assetid");
							var node = $(this);
							var categoryid = node.attr("nodeid");
							
							
//							var tree = this.nearest(".categorytree");
//							var treeid = tree.data("")
							//toggleNode('users','categoryPickerTree_media/catalogs/public_admin','users')
							//this is a category
							jQuery.get("$home$apphome/components/categorize/addassetcategory.html", 
									{
										assetid:assetid,
										categoryid:categoryid
									},
									function(data) 
									{
										node.find("div").append("<span class='fader'>&nbsp;+1</span>");
										node.find(".fader").fadeOut(3000);
									}
							);

						},
						tolerance: 'pointer',
						over: outlineSelectionCol,
						out: unoutlineSelectionCol
					}
				);
			}
		);

		jQuery(".autosubmitdetails").livequery(
			function()
			{
				jQuery(this).find(".autosubmited").change(
				  function() 
				  {
					  jQuery(this).parents("form").submit();
				  }
				);
				
			}
		);		
		jQuery(".emfadeout").livequery(
			function()
			{
				jQuery(this).fadeOut(3000, function() 
				 {
					jQuery(this).html("");
				 });
			}
		);	
		jQuery(".ajaxstatus").livequery(
				function()
				{
	
					var uid = $(this).attr("id");
					
					var running = runningstatus[uid];
					if( !running)
					{
						runningstatus[uid] = true;
						var timeout = $(this).attr("period")
						if( !timeout)
						{
							timeout = "5000";
						}
						timeout = parseInt(timeout);
						setTimeout('showajaxstatus("' + uid +'",' + timeout + ');',timeout);
					}
				}
		);
}

var runningstatus = {};

showajaxstatus = function(uid, timeout)
{
	//for each asset on the page reload it's status
	var foundone = false;
	var cell = jQuery("#" + uid);
	if( cell )
	{
		var path = cell.attr("ajaxpath");
		jQuery.get(path, {}, function(data) {
			cell.replaceWith(data);
			cell = jQuery("#" + uid);
			if( cell.length > 0 )
			{
				setTimeout('showajaxstatus("' + uid +'",' + timeout + ');',timeout);
			}
			else
			{
				delete runningstatus[uid];
			}
		});
	}
}



jQuery(document).ready(function() 
{ 
	jQuery.ajaxSetup({
	    cache: false
	});

	$(document).ajaxError(function(e, jqxhr, settings, exception) 
			{
				var errordiv = jQuery("#errorinfoarea")
				if( errordiv.length > 0)
				{
				    $(errordiv).html("Error " + settings.url + " returned " + exception);

				}
				else
				{
					  alert("Error \n" + settings.url + " \nreturned " + exception);

				}
			});

	onloadselectors();
	emcomponents();
	
	
}); 

emcomponents = function() {
	$("#savedquerylist a").click(function(e)
			{
				e.preventDefault();
				var a = jQuery(this);
				var link = a.attr("href");
				
				jQuery.get(link, {}, function(data) 
						{
							var toreplace = jQuery("#searcheditor");
							toreplace.html(data);
							
							var tmp = jQuery("#savedquerylist #newterm");
							tmp.remove();
							var top = a.position().top;
							top = top + a.height() + 40;
							jQuery("#eml-green-dialog").css("top",top);
							
							jQuery("#arrow").show();
							var padleft = a.position().left;
							padleft = padleft + a.width() / 2;
							padleft = padleft  - 42; //arrow width
							jQuery("#arrow").css("left",padleft);
						}
				);
				return false;
			}
	);
	
	$("#addterm").click(function(e)
		{
			e.preventDefault();
			var a = jQuery(this);
			var link = a.attr("href");
			
			jQuery.get(link, {}, function(data) 
					{
						var toreplace = jQuery("#searcheditor");
						toreplace.html(data);
						
						jQuery("#savedquerylist span").append('<span id="newterm">new term</span>');
						var a = jQuery("#savedquerylist #newterm");
						var top = a.position().top;
						top = top + a.height() + 40;
						jQuery("#eml-green-dialog").css("top",top);
						
						jQuery("#arrow").show();
						var padleft = a.position().left;
						padleft = padleft + a.width() / 2;
						padleft = padleft  - 42; //arrow width
						jQuery("#arrow").css("left",padleft);
					}
			);
			return false;
		}
	);
}

