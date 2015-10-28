package com.lucasian.repository.taglib

import org.thymeleaf.Arguments
import org.thymeleaf.dom.Element
import org.thymeleaf.processor.IAttributeNameProcessorMatcher
import org.thymeleaf.processor.attr.AbstractTextChildModifierAttrProcessor
import org.thymeleaf.processor.attr.AbstractUnescapedTextChildModifierAttrProcessor

/**
 * Created by blzb on 10/26/15.
 */
class RepositoryProcessor extends AbstractUnescapedTextChildModifierAttrProcessor {

    public RepositoryProcessor(){
        super('path')
    }

    public int getPrecedence() {
    // A value of 10000 is higher than any attribute in the
    // SpringStandard dialect. So this attribute will execute
    // after all other attributes from that dialect, if in the
    // same tag.
    return 10000;
  }

  //
  // Our processor is a subclass of the convenience abstract implementation
  // 'AbstractTextChildModifierAttrProcessor', which takes care of the
  // DOM modifying stuff and allows us just to implement this 'getText(...)'
  // method to compute the text to be set as tag body.
  //
  @Override
  protected String getText(final Arguments arguments, final Element element,
                           final String attributeName) {
    return '''
            <div class="row">
                <div class="col-lg-3">
                    <div class="ibox float-e-margins">
                        <div class="ibox-content">
                            <div class="file-manager">
                                <div class="pull-right">
                                    <button rel="tooltip" data-toggle="tooltip" data-placement="top" data-original-title="Subir más archivos" onclick="subirArchivo()" class="btn btn-outline btn-primary dim" type="button"><i class="fa fa-cloud-upload"></i></button>
                                    <button rel="tooltip" data-toggle="tooltip" data-placement="top" data-original-title="Crear nueva carpeta" class="btn btn-outline btn-primary dim" type="button"><i class="fa fa-folder-open-o"></i></button>
                                    <button rel="tooltip" data-toggle="tooltip" data-placement="top" data-original-title="Mostrar eliminados" class="btn btn-outline btn-primary dim" type="button"><i class="fa fa-trash-o"></i></button>
                                </div>
                                <div class="clearfix"></div>
                                <div class="hr-line-dashed"></div>
                                <ul class="folder-list" style="padding: 0">
                                    <li><a href="#"><i class="fa fa-folder"></i>Folder</a></li>
                                </ul>
                                <h5 class="tag-title">Etiquetas</h5>
                                <ul class="tag-list" style="padding: 0">
                                    <li><a href="#"><i class="fa fa-tag"></i> Tag</a></li>
                                </ul>
                                <div class="clearfix"></div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-lg-9 animated fadeInRight">
                    <div class="row wrapper white-bg">
                        <div class="col-lg-10">
                            <ol class="breadcrumb">
                                <li>
                                    <i class="fa fa-folder-open"></i> &nbsp;<a href="#">Home</a>
                                </li>
                                <li class="active">
                                    <span class="text-success">
                                        <strong>Mis documentos</strong>
                                    </span>
                                </li>
                            </ol>
                        </div>
                        <div class="col-lg-2">
                        </div>
                    </div>
                    <br/>
                    <div class="row">
                        <div class="col-lg-12">
                            <div class="file-box">
                                <div class="file">
                                    <a href="#">
                                        <span class="corner"></span>

                                        <div class="icon">
                                            <i class="fa fa-file"></i>
                                        </div>
                                        <div class="file-name">
                                            Documento_2014.doc
                                            <br/>
                                            <small>Agregado: Ene 11, 2014</small>
                                        </div>
                                    </a>
                                </div>
                            </div>

                        </div>
                    </div>

                </div>
            </div>
        <div class="modal inmodal" id="dropModal" tabindex="-1" role="dialog" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content animated flipInY">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
                        <h4 class="modal-title">Agregar documentos</h4>
                        <small class="font-bold">Aquí puedes agregar documentos arrasttrándolos o dando click en el recuadro.</small>
                    </div>
                    <div class="modal-body">

                        <div>
                            <form id="my-awesome-dropzone" class="dropzone" action="#">
                                <div class="dz-message" data-dz-message><strong>No has subido ningún documento</strong></div>
                                <div class="dropzone-previews"></div>
                                <button type="submit" class="btn btn-info pull-right">Guardar documentos!</button>
                            </form>
                        </div>

                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-primary" data-dismiss="modal">Cerrar</button>
                    </div>
                </div>
            </div>
        </div>
        <!--
            JS Behavior
        -->
            <!--Dropzone JS File-->
            <script src="/inspinia/js/plugins/dropzone/dropzone.js"></script>
            <script src="/inspinia/js/plugins/dropzone/dropzone.js"></script>
            <script>
                //Modal Config
                function subirArchivo() {
                    $("#dropModal").modal("toggle");
                }

                //Dropzone Config
                $(document).ready(function () {
                    Dropzone.options.myAwesomeDropzone = {
                        autoProcessQueue: false,
                        uploadMultiple: true,
                        parallelUploads: 100,
                        maxFiles: 100,
                        // Dropzone settings
                        init: function () {
                            var myDropzone = this;
                            this.element.querySelector("button[type=submit]").addEventListener("click", function (e) {
                                e.preventDefault();
                                e.stopPropagation();
                                myDropzone.processQueue();
                            });
                            this.on("sendingmultiple", function () {
                            });
                            this.on("successmultiple", function (files, response) {
                            });
                            this.on("errormultiple", function (files, response) {
                            });
                        }
                    };
                });

            function subirArchivo() {
                $("#dropModal").modal("toggle");
            }

            //Dropzone
            $(document).ready(function () {
                Dropzone.options.myAwesomeDropzone = {
                    autoProcessQueue: false,
                    uploadMultiple: true,
                    parallelUploads: 100,
                    maxFiles: 100,
                    // Dropzone settings
                    init: function () {
                        var myDropzone = this;
                        this.element.querySelector("button[type=submit]").addEventListener("click", function (e) {
                            e.preventDefault();
                            e.stopPropagation();
                            myDropzone.processQueue();
                        });
                        this.on("sendingmultiple", function () {
                        });
                        this.on("successmultiple", function (files, response) {
                        });
                        this.on("errormultiple", function (files, response) {
                        });
                    }

                };
            });
        </script>
    '''
  }
}
