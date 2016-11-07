(function() {
    'use strict';
    angular
        .module('eLancerApp')
        .factory('Services', Services);

    Services.$inject = ['$resource', 'DateUtils'];

    function Services ($resource, DateUtils) {
        var resourceUrl =  'api/services/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.datePosted = DateUtils.convertLocalDateFromServer(data.datePosted);
                        data.expirationDate = DateUtils.convertLocalDateFromServer(data.expirationDate);
                    }
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.datePosted = DateUtils.convertLocalDateToServer(copy.datePosted);
                    copy.expirationDate = DateUtils.convertLocalDateToServer(copy.expirationDate);
                    return angular.toJson(copy);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.datePosted = DateUtils.convertLocalDateToServer(copy.datePosted);
                    copy.expirationDate = DateUtils.convertLocalDateToServer(copy.expirationDate);
                    return angular.toJson(copy);
                }
            }
        });
    }
})();
