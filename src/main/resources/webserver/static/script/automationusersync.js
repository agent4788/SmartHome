        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // schaltbares Element schalten ////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        function switchDevice(roomId, devId, command) {

            $.getJSON('/automation/switch?roomid=' + roomId + '&id=' + devId + '&command=' + command)
                .done(function(data, textStatus) {

                    if(textStatus == 'success' && data.success == true) {

                        //Befehl erfolgreich ausgeführt
                        $('body').toast({
                            class: 'success',
                            showIcon: 'check circle outline',
                            message: `Befehl erfolgreich ausgeführt`
                        });
                    } else {

                        //Fehler
                        $('body').toast({
                            class: 'error',
                            showIcon: 'times circle outline',
                            message: data.message
                        });
                    }
                })
                .fail(function () {

                    //Fehler
                    $('body').toast({
                        class: 'error',
                        showIcon: 'times circle outline',
                        message: `Der Befehl konnte nicht ausgeführt werden`
                    });
                });
        }

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Rollladen bewegen ///////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        function moveShutter(roomId, elementId, targetLevel) {

            $.getJSON('/automation/moveshutter?roomid=' + roomId + '&id=' + elementId + '&level=' + targetLevel)
                .done(function(data, textStatus) {

                    if(textStatus == 'success' && data.success == true) {

                        //Befehl erfolgreich ausgeführt
                        $('body').toast({
                            class: 'success',
                            showIcon: 'check circle outline',
                            message: `Befehl erfolgreich ausgeführt`
                        });
                    } else {

                        //Fehler
                        $('body').toast({
                            class: 'error',
                            showIcon: 'times circle outline',
                            message: data.message
                        });
                    }
                })
                .fail(function () {

                    //Fehler
                    $('body').toast({
                        class: 'error',
                        showIcon: 'times circle outline',
                        message: `Der Befehl konnte nicht ausgeführt werden`
                    });
                });
        }

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Button Synchronisieren //////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        function syncButton(element) {

            if(element.state) {

                //an
                var buttonOn = $('#a' + element.id + '_on');
                var buttonOff = $('#a' + element.id + '_off');
                var onText = buttonOn.text();
                var offText = buttonOff.text();
                buttonOn.html('<i class="check icon"></i>' + onText);
                buttonOff.html(offText);
            } else {

                //aus
                var buttonOn = $('#a' + element.id + '_on');
                var buttonOff = $('#a' + element.id + '_off');
                var onText = buttonOn.text();
                var offText = buttonOff.text();
                buttonOn.html(onText);
                buttonOff.html('<i class="check icon"></i>' + offText);
            }
        }

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Sensor Synchronisieren //////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        function syncSensor(element) {

            $('#a' + element.id).text(element.value);
        }

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // virtuellen Sensor Synchronisieren ///////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        function syncVirtualSensor(element) {

            if(element.avg) {

                $('#a' + element.id + '_avg').text(element.avg);
            }
            if(element.sum) {

                $('#a' + element.id + '_sum').text(element.sum);
            }
            if(element.min) {

                $('#a' + element.id + '_min').text(element.min);
            }
            if(element.max) {

                $('#a' + element.id + '_max').text(element.max);
            }
        }

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Rollladen Synchronisieren ///////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        var shutterLastStateMap = new Map();
        var shutterLastStateChangeTimeMap = new Map();
        function syncShutter(element) {

            //Differenzzeit berechnen
            var calculateDiffTime = function() {

                var diffTime = 0;
                if(shutterLastStateChangeTimeMap.has(id)) {

                    var diffTime = Date.now() - shutterLastStateChangeTimeMap.get(id);
                }
                return diffTime;
            }

            //Niveau anzeige aktualisieren
            var updateLevel = function(input, level) {

                if(level == 0) {
                    input.val("offen");
                } else if(level == 100) {
                     input.val("geschlossen");
                } else {
                     input.val(level + " %");
                }
            }

            var id = element.id.trim();
            var level = element.level;

            var buttonClose = $('#a' + id + '_close');
            var buttonOpen = $('#a' + id + '_open');
            var buttonSlider = $('#a' + id + '_sliderbutton');
            var slider = $('#a' + id + '_slider');
            var sliderTarget = $('#a' + id + '_slider_value_target');
            var sliderCurrent = $('#a' + id + '_slider_value_current');

            if(level == 0) {

                //offen
                buttonOpen.addClass('blue');
                buttonOpen.addClass('disabled');
                buttonClose.removeClass('blue');
                buttonClose.removeClass('disabled');
                buttonSlider.removeClass('blue');
                buttonSlider.empty().html('<i class="bars icon"></i>');
                updateLevel(sliderCurrent, level);

                var diffTime = calculateDiffTime();
                if(shutterLastStateChangeTimeMap.has(id) && diffTime > 1500 && diffTime < 2500) {

                    slider.removeClass('disabled');
                    buttonClose.removeClass('disabled');
                    buttonOpen.addClass('disabled');
                    slider.slider('set value', level);
                    updateLevel(sliderTarget, level);
                } else if(shutterLastStateChangeTimeMap.has(id) && diffTime > 0 && diffTime < 1500) {

                    slider.addClass('disabled');
                    buttonClose.addClass('disabled');
                    buttonOpen.addClass('disabled');
                    slider.slider('set value', level);
                }

            } else if(level == 100) {

                //geschlossen
                buttonOpen.removeClass('blue');
                buttonOpen.removeClass('disabled');
                buttonClose.addClass('blue');
                buttonClose.addClass('disabled');
                buttonSlider.removeClass('blue');
                buttonSlider.empty().html('<i class="bars icon"></i>');
                updateLevel(sliderCurrent, level);

                var diffTime = calculateDiffTime();
                if(shutterLastStateChangeTimeMap.has(id) && diffTime > 1500 && diffTime < 2500) {

                    slider.removeClass('disabled');
                    buttonClose.addClass('disabled');
                    buttonOpen.removeClass('disabled');
                    slider.slider('set value', level);
                    updateLevel(sliderTarget, level);
                } else if(shutterLastStateChangeTimeMap.has(id) && diffTime > 0 && diffTime < 1500) {

                    slider.addClass('disabled');
                    buttonClose.addClass('disabled');
                    buttonOpen.addClass('disabled');
                    slider.slider('set value', level);
                }
            } else {

                //dazwischen
                buttonOpen.removeClass('blue');
                buttonOpen.removeClass('disabled');
                buttonClose.removeClass('blue');
                buttonClose.removeClass('disabled');
                buttonSlider.addClass('blue');
                buttonSlider.empty().text(level + ' %');
                updateLevel(sliderCurrent, level);

                var diffTime = calculateDiffTime();
                if(shutterLastStateChangeTimeMap.has(id) && diffTime > 1500 && diffTime < 2500) {

                    slider.removeClass('disabled');
                    buttonClose.removeClass('disabled');
                    buttonOpen.removeClass('disabled');
                    slider.slider('set value', level);
                    updateLevel(sliderTarget, level);
                } else if(shutterLastStateChangeTimeMap.has(id) && diffTime > 0 && diffTime < 1500) {

                    slider.addClass('disabled');
                    buttonClose.addClass('disabled');
                    buttonOpen.addClass('disabled');
                    slider.slider('set value', level);
                }
            }

            //Status setzen nach dem neu laden der Seite
            if(!shutterLastStateMap.has(id)) {

                slider.slider('set value', level);
                updateLevel(sliderTarget, level);
            }

            //prüfen ob Status geändert
            if(shutterLastStateMap.has(id) && shutterLastStateMap.get(id) != level) {

                //Zeitstempel der letzten Änderung speichern
                shutterLastStateChangeTimeMap.set(id, Date.now());
            }

            //aktuellen Status des Rollos speichern
            shutterLastStateMap.set(id, level);
        }

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Synchronisation der Elemente ////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        var syncActive = false;
        function sync(roomId) {

            if (window.EventSource) {

                //Synchronisation per SSE
                var eventSource = new EventSource('/automation/ssesync?roomid=' + roomId, { withCredentials : true });
                eventSource.onmessage = function(entry) {

                    //JOSN parsen
                    var data = jQuery.parseJSON(entry.data.trim());

                    //Synchronisation aktiv
                    syncActive = true;

                    //Buttons aktualisieren
                    if(data.buttons && data.buttons.length > 0) {

                        for(var i = 0; i < data.buttons.length; i++) {

                            var e = data.buttons[i];
                            syncButton(e);
                        }
                    }

                    //Sensorwerte aktualisieren
                    if(data.sensors && data.sensors.length > 0) {
                         for(var i = 0; i < data.sensors.length; i++) {

                             var e = data.sensors[i];
                             syncSensor(e);
                        }
                    }

                    //Virtuelle Sensorwerte aktualisieren
                    if(data.vSensors && data.vSensors.length > 0) {

                        for(var i = 0; i < data.vSensors.length; i++) {

                            var e = data.vSensors[i];
                            syncVirtualSensor(e);
                        }
                    }

                    //Rollläden aktualisieren
                    if(data.shutters && data.shutters.length > 0) {

                        for(var i = 0; i < data.shutters.length; i++) {

                            var e = data.shutters[i];
                            syncShutter(e);
                        }
                    }

                    //Sychronisation abgeschlossen
                    syncActive = false;
                }
            }
        }