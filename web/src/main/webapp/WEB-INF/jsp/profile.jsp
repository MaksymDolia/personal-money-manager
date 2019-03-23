<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ include file="../layouts/taglib.jsp" %>
<div class="row">
    <div class="col-md-10">
        <p>
            You registered as <strong><security:authentication
                property="principal.username"/></strong>
        </p>
    </div>
    <div class="col-md-2">
        <!-- Delete profile button trigger modal -->
        <a href='<spring:url value="/profile/delete_profile" />' type="button"
           class="btn btn-danger trigger-remove" data-target="#remove-modal">Delete
            profile </a>
    </div>
</div>

<!-- Delete Profile Modal -->
<div class="modal fade" id="remove-modal" tabindex="-1" role="dialog"
     aria-labelledby="remove-modal">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="myModalLabel">Delete Profile</h4>
            </div>
            <div class="modal-body">
                <p>You are about to delete your profile, this procedure is
                    irreversible.</p>
                <p>Do you want to proceed?</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                <a id="btn-remove" href="" type="button" class="btn btn-danger">Delete
                    profile</a>
            </div>
        </div>
    </div>
</div>