(function() {
    'use strict';
    angular
        .module('eLancerApp')
        .factory('Profile', Profile);

    Profile.$inject = ['$resource', 'DateUtils'];

    function Profile ($resource, DateUtils) {
        var resourceUrl =  'api/profile/services/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();

(function() {
    'use strict';
    angular
        .module('eLancerApp')
        .factory('AccountHelp', AccountHelp);

    AccountHelp.$inject = ['$resource', 'DateUtils'];

    function AccountHelp ($resource, DateUtils) {
        var resourceUrl =  'api/accountHelp';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: false}
        });
    }
})();
