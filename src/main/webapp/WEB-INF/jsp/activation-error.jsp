<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta
            name="viewport"
            content="width=device-width, initial-scale=1, shrink-to-fit=no">
        <meta name="description" content="">
        <meta name="author" content="">
        <title>Gemalto - Activation</title>

        <!-- Bootstrap core CSS -->
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
        <link
            rel="stylesheet"
            href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css"
            integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm"
            crossorigin="anonymous">
        <style>
        	body{
        		overflow: hidden;
        	}
            #act-main-sec {
            	height: 90vh !important;
                background-image: url(<spring:eval expression="@environment.getProperty('portal.url')" />/src/img/loginbackground.jpg);
				background-size: cover;
                
            }
            #act-content-sec{
                background: none !important;
            }
            .bg-custom{           
                background: #fff;
                background: -moz-linear-gradient(top,#fff 0,#fff 20%,#f0f0f0 100%)!important;
                background: -webkit-gradient(left top,left bottom,color-stop(0,#fff),color-stop(20%,#fff),color-stop(100%,#f0f0f0))!important;
                background: -webkit-linear-gradient(top,#fff,#fff 20%,#f0f0f0)!important;
                background: -o-linear-gradient(top,#fff 0,#fff 20%,#f0f0f0 100%)!important;
                background: -ms-linear-gradient(top,#fff 0,#fff 20%,#f0f0f0 100%)!important;
                background: linear-gradient(180deg,#fff 0,#fff 20%,#f0f0f0)!important;
                filter: progid: DXImageTransform.Microsoft.gradient(startColorstr="#ffffff",endColorstr="#f0f0f0",GradientType=0)!important;
                border-top: 22px solid red!important;
            }
            #custom-img{
                width: 20% !important;
                margin-left:2%;
            }
            .act-img-padd{
                padding: 1rem !important;
            }
            .act-content-div{
               	margin-left: 24%;
				margin-right: 24%;
				margin-top: 10%;
				margin-bottom: 18%;
				background-color: #ffffff;
				color: #6b6b6b;
				padding: 2%;

            }

        </style>

    </head>

    <body>

        <header>
            <div class="bg-custom" id="navbarHeader">
                <div>
                    <div class="row">
                        <div class="col-sm-8 col-md-7 act-img-padd">
                            <!-- <h4 class="text-white">Gemalto</h4> -->
                            <img src="<spring:eval expression="@environment.getProperty('logo.path')" />" class="img-fluid" id="custom-img" alt="GEMALTO">
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </header>

    <main role="main" id="act-main-sec">
        <section class="jumbotron text-center" id="act-content-sec">
            <div class="container">
                <div class="act-content-div">
                <i class="fa fa-times-circle-o" style="font-size:48px;color:#dc3545;"></i>
                <h3 class="jumbotron-heading">Invalid Link!</h3>
                <p class="lead">Either the link has expired or your account is already acivated!</p>
                <p class="lead">Please click on continue to access login page.</p>
                <p>
                    <a
                        href="<spring:eval expression="@environment.getProperty('portal.url')" />"
                        class="btn btn-danger my-2">Continue</a>
                </p>
            </div>
            </div>
        </section>
    </main>

    <!-- Bootstrap core JavaScript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script
        src="https://code.jquery.com/jquery-3.2.1.slim.min.js"
        integrity="sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN"
        crossorigin="anonymous"></script>
    <script
        src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js"
        integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q"
        crossorigin="anonymous"></script>
    <script
        src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js"
        integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl"
        crossorigin="anonymous"></script>

</body>
</html>