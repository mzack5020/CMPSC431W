(function() {
    'use strict';
    angular
        .module('eLancerApp')
        .factory('SortedBids', SortedBids);

    SortedBids.$inject = ['$resource', 'DateUtils'];

    function SortedBids ($resource, DateUtils) {
        var resourceUrl =  'api/sorted-bids/:id';

        return $resource(resourceUrl, {}, {
            'get': { method: 'GET', isArray: true }
        });
    }
})();
