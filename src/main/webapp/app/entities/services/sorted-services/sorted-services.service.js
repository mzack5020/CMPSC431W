(function() {
    'use strict';
    angular
        .module('eLancerApp')
        .factory('SortedServices', SortedServices);

    SortedServices.$inject = ['$resource', 'DateUtils'];

    function SortedServices ($resource, DateUtils) {
        var resourceUrl =  'api/sorted-services/:id';

        return $resource(resourceUrl, {}, {
            'get': { method: 'GET', isArray: true }
        });
    }
})();
